package com.rlj.common.mybatis;

import com.rlj.api.message.Message;
import com.rlj.common.util.FastJsonConvertUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Renlingjie
 * @name
 * @date 2021-09-30
 */
public class MessageJsonTypeHandler extends BaseTypeHandler<Message> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Message message, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, FastJsonConvertUtil.convertObjectToJSON(message));
    }

    @Override
    public Message getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        String value = resultSet.getString(columnName);
        if (null != value && !StringUtils.isBlank(value)){
            return FastJsonConvertUtil.convertJSONToObject(resultSet.getString(columnName),Message.class);
        }
        return null;
    }

    @Override
    public Message getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        String value = resultSet.getString(columnIndex);
        if (null != value && !StringUtils.isBlank(value)){
            return FastJsonConvertUtil.convertJSONToObject(resultSet.getString(columnIndex),Message.class);
        }
        return null;
    }

    @Override
    public Message getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        String value = callableStatement.getString(columnIndex);
        if (null != value && !StringUtils.isBlank(value)){
            return FastJsonConvertUtil.convertJSONToObject(callableStatement.getString(columnIndex),Message.class);
        }
        return null;
    }
}
