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
    public long next(String group, String prefix) {
        return increment(group, prefix, 1);
    }

    @Override
    public String next(String group, String prefix, int length) {
        return IdUtil.addZero(prefix, String.valueOf(increment(group, prefix, 1)), length);
    }

    @Override
    public long increment(String group, String prefix, int incr) {
        Asserts.notNull(group, "group" + Asserts.NotNullMessage);
        Asserts.notNull(prefix, "prefix" + Asserts.NotNullMessage);
        Connection conn = null;
        try {
            conn = this.ds.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "update serial set `number`=@next:=`number`+? where id=? and prefix=?");
            ps.setLong(1, incr);
            ps.setString(2, group);
            ps.setString(3, prefix);
            Asserts.isTrue(ps.executeUpdate() > 0, prefix + " not found");
            ResultSet rs = ps.executeQuery("select @next");
            if (rs.next()) {
                return rs.getLong(1);
            }
            throw new RuntimeException("group:" + group + ",prefix:" + prefix + " not found");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("group:" + group + ",prefix:" + prefix + " increment failed");
        } finally {
            closeConnection(conn);
        }
    }


    @Override
    public boolean exist(String group, String prefix) {
        Asserts.notNull(group, "group" + Asserts.NotNullMessage);
        Asserts.notNull(prefix, "prefix" + Asserts.NotNullMessage);

        Connection conn = null;
        try {
            conn = this.ds.getConnection();
            PreparedStatement ps = conn.prepareStatement("select count(*) from serial where id=? and prefix=?");
            ps.setString(1, group);
            ps.setString(2, prefix);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(conn);
        }
        return false;
    }

    @Override
    public int save(String group, String prefix) {
        Asserts.notNull(group, "group" + Asserts.NotNullMessage);
        Asserts.notNull(prefix, "prefix" + Asserts.NotNullMessage);
        if (exist(group, prefix)) {
            return 0;
        }

        Connection conn = null;
        try {
            conn = this.ds.getConnection();
            PreparedStatement ps = conn.prepareStatement("insert serial(id,prefix,`number`)values(?,?,0)");
            ps.setString(1, group);
            ps.setString(2, prefix);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(conn);
        }
        return 0;
    }

    @Override
    public int remove(String group, String prefix) {
        Asserts.notNull(group, "group" + Asserts.NotNullMessage);
        Asserts.notNull(prefix, "prefix" + Asserts.NotNullMessage);
        Connection conn = null;
        try {
            conn = this.ds.getConnection();
            PreparedStatement ps = conn.prepareStatement("delete from serial where id=? and prefix=?");
            ps.setString(1, group);
            ps.setString(2, prefix);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(conn);
        }
        return 0;
    }

    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

