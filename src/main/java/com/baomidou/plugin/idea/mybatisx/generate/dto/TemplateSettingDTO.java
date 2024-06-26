package com.baomidou.plugin.idea.mybatisx.generate.dto;

import lombok.Getter;

import java.io.Serializable;
import java.util.Set;

@Getter
public class TemplateSettingDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 配置名称
     */
    private String configName;
    /**
     * 配置文件名称
     */
    private String configFile;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 后缀
     */
    private String suffix;
    /**
     * 包名
     */
    private String packageName;
    /**
     * 编码
     */
    private String encoding;
    /**
     * 相对模块的资源文件路径
     */
    private String basePath;

    /**
     * 是否启用
     */
    private Set<String> existsFileNames;

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setExistsFileNames(Set<String> existsFileNames) {
        this.existsFileNames = existsFileNames;
    }
}
