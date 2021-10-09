package com.rlj.task.enums;

/**
 * @author Renlingjie
 * @name
 * @date 2021-09-26
 */
public enum ElasticJobTypeEnum {
    SIMPLE("SimpleJob","简单类型Job"),
    DATAFLOW("DataflowJob","流式类型Job"),
    SCRIPT("ScriptJob","脚本类型Job");
    private String type;
    private String description;
    private ElasticJobTypeEnum(String type,String description){
        this.type = type;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
