package pers.clare.bufferid.manager.impl;

import pers.clare.bufferid.exception.FormatRuntimeException;
import pers.clare.bufferid.manager.AbstractIdManager;
import pers.clare.bufferid.util.IdUtil;
import pers.clare.util.Asserts;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLIdManager extends AbstractIdManager {
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
    protected long doIncrement(String id, String prefix, long incr) {
        Asserts.notNull(id, "id");
        Asserts.notNull(prefix, "prefix");
        try (
                Connection conn = this.ds.getConnection()
        ) {
            PreparedStatement ps = conn.prepareStatement(
                    "update serial set `number`=@next:=`number`+? where id=? and prefix=?");
            ps.setLong(1, incr);
            ps.setString(2, id);
            ps.setString(3, prefix);
            if (ps.executeUpdate() == 0) {
                throw new FormatRuntimeException("id=%s,prefix=%s not found", id, prefix);
            }
            ResultSet rs = ps.executeQuery("select @next");
            if (rs.next()) {
                return rs.getLong(1);
            }
            throw new FormatRuntimeException("id=%s,prefix=%s not found", id, prefix);
        } catch (SQLException e) {
            throw new FormatRuntimeException("id=%s,prefix=%s increment failed", id, prefix);
        }
    }


    @Override
    public boolean exist(String id, String prefix) {
        Asserts.notNull(id, "id");
        Asserts.notNull(prefix, "prefix");

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
        Asserts.notNull(id, "id");
        Asserts.notNull(prefix, "prefix");
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
        Asserts.notNull(id, "id");
        Asserts.notNull(prefix, "prefix");

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

