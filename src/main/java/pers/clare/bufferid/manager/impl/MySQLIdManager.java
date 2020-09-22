package pers.clare.bufferid.manager.impl;

import pers.clare.bufferid.manager.IdManager;
import pers.clare.bufferid.util.IdUtil;
import pers.clare.util.Asserts;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLIdManager implements IdManager {
    private DataSource ds;

    public MySQLIdManager(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public long next(String id, String prefix) {
        return increment(id, prefix, 1);
    }

    @Override
    public String next(String id, String prefix, int length) {
        return IdUtil.addZero(prefix, String.valueOf(increment(id, prefix, 1)), length);
    }

    @Override
    public long increment(String id, String prefix, int incr) {
        Asserts.notNull(id, "id" + Asserts.NotNullMessage);
        Asserts.notNull(prefix, "prefix" + Asserts.NotNullMessage);
        try (
                Connection conn = this.ds.getConnection()
        ) {
            PreparedStatement ps = conn.prepareStatement(
                    "update serial set `number`=@next:=`number`+? where id=? and prefix=?");
            ps.setLong(1, incr);
            ps.setString(2, id);
            ps.setString(3, prefix);
            Asserts.isTrue(ps.executeUpdate() > 0, prefix + " not found");
            ResultSet rs = ps.executeQuery("select @next");
            if (rs.next()) {
                return rs.getLong(1);
            }
            throw new RuntimeException("id:" + id + ",prefix:" + prefix + " not found");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("id:" + id + ",prefix:" + prefix + " increment failed");
        }
    }


    @Override
    public boolean exist(String id, String prefix) {
        Asserts.notNull(id, "id" + Asserts.NotNullMessage);
        Asserts.notNull(prefix, "prefix" + Asserts.NotNullMessage);

        try (
                Connection conn = this.ds.getConnection()
        ) {
            PreparedStatement ps = conn.prepareStatement("select count(*) from serial where id=? and prefix=?");
            ps.setString(1, id);
            ps.setString(2, prefix);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int save(String id, String prefix) {
        Asserts.notNull(id, "id" + Asserts.NotNullMessage);
        Asserts.notNull(prefix, "prefix" + Asserts.NotNullMessage);
        if (exist(id, prefix)) {
            return 0;
        }

        try (
                Connection conn = this.ds.getConnection()
        ) {
            PreparedStatement ps = conn.prepareStatement("insert serial(id,prefix,`number`)values(?,?,0)");
            ps.setString(1, id);
            ps.setString(2, prefix);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int remove(String id, String prefix) {
        Asserts.notNull(id, "id" + Asserts.NotNullMessage);
        Asserts.notNull(prefix, "prefix" + Asserts.NotNullMessage);

        try (
                Connection conn = this.ds.getConnection()
        ) {
            PreparedStatement ps = conn.prepareStatement("delete from serial where id=? and prefix=?");
            ps.setString(1, id);
            ps.setString(2, prefix);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

