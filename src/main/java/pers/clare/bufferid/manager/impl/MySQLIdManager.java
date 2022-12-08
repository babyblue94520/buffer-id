package pers.clare.bufferid.manager.impl;

import pers.clare.bufferid.exception.BufferIdIllegalArgumentException;
import pers.clare.bufferid.exception.BufferIdSQLException;
import pers.clare.bufferid.manager.AbstractIdManager;
import pers.clare.bufferid.util.DataSourceSchemaUtil;
import pers.clare.bufferid.util.Asserts;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLIdManager extends AbstractIdManager {
    private static final String updateIncrement = "update serial set `number`=@next:=`number`+? where id=? and prefix=?";
    private static final String findNext = "select @next";
    private static final String findCount = "select count(*) from serial where id=? and prefix=?";
    private static final String insert = "insert into serial values(?,?,0)";
    private static final String delete = "delete from serial where id=? and prefix=?";

    private final DataSource dataSource;

    public MySQLIdManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void initSchema() {
        try {
            DataSourceSchemaUtil.init(dataSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long next(String id, String prefix) {
        return increment(id, prefix, 1);
    }

    @Override
    protected long doIncrement(String id, String prefix, long incr) {
        Asserts.notNull(id, "id");
        Asserts.notNull(prefix, "prefix");
        try (Connection conn = this.dataSource.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(updateIncrement);
            ps.setLong(1, incr);
            ps.setString(2, id);
            ps.setString(3, prefix);
            ps.executeUpdate();
            ResultSet rs = conn.createStatement().executeQuery(findNext);
            if (rs.next()) {
                return rs.getLong(1);
            }
            if (ps.getUpdateCount() == 0) {
                throw new BufferIdIllegalArgumentException("id=%s, prefix=%s not found", id, prefix);
            }

            throw new BufferIdIllegalArgumentException("id=%s, prefix=%s not found", id, prefix);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new BufferIdSQLException(e, "id=%s, prefix=%s increment failed", id, prefix);
        }
    }


    @Override
    public boolean exist(String id, String prefix) {
        Asserts.notNull(id, "id");
        Asserts.notNull(prefix, "prefix");

        try (Connection conn = this.dataSource.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(findCount);
            ps.setString(1, id);
            ps.setString(2, prefix);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new BufferIdSQLException(e, "id=%s, prefix=%s increment failed", id, prefix);
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

        try (Connection conn = this.dataSource.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(insert);
            ps.setString(1, id);
            ps.setString(2, prefix);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new BufferIdSQLException(e, "id=%s, prefix=%s save failed", id, prefix);
        }
    }

    @Override
    public int remove(String id, String prefix) {
        Asserts.notNull(id, "id");
        Asserts.notNull(prefix, "prefix");
        try (Connection conn = this.dataSource.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(delete);
            ps.setString(1, id);
            ps.setString(2, prefix);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new BufferIdSQLException(e, "id=%s, prefix=%s remove failed", id, prefix);
        }
    }
}

