package io.baji.stvh.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.baji.stvh.entity.Meta;
import lombok.SneakyThrows;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class MetaTypeHandler  extends BaseTypeHandler<Meta> {
    @SneakyThrows
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Meta parameter, JdbcType jdbcType) throws SQLException {
        ObjectMapper mapper = new ObjectMapper();
        String s = mapper.writeValueAsString(parameter);
        if (Objects.nonNull(s)) {
            ps.setString(i, s);
        }
    }

    @Override
    public Meta getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String string = rs.getString(columnName);
        Meta meta = null;
        if (Objects.nonNull(string)) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                meta = mapper.readValue(string, Meta.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return meta;

    }

    @Override
    public Meta getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String string = rs.getString(columnIndex);
        Meta meta = null;
        if (Objects.nonNull(string)) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                meta = mapper.readValue(string, Meta.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return meta;
    }

    @Override
    public Meta getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String string = cs.getString(columnIndex);
        Meta meta = null;
        if (Objects.nonNull(string)) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                meta = mapper.readValue(string, Meta.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return meta;
    }
}
