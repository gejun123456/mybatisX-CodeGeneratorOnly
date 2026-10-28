package com.baomidou.plugin.idea.mybatisx.generate.action;


import com.baomidou.plugin.idea.mybatisx.generate.dto.GenerateConfig;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TableUIInfo;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateContext;
import com.baomidou.plugin.idea.mybatisx.generate.notify.Notifier;
import com.baomidou.plugin.idea.mybatisx.generate.setting.TemplatesSettings;
import com.baomidou.plugin.idea.mybatisx.generate.template.GenerateCode;
import com.intellij.database.model.DasObject;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Mybatis generator main action.
 */
public class MybatisGeneratorMainAction extends AnAction {

    public static final String TITLE = "MybatisX Generate";
    private static final Logger logger = LoggerFactory.getLogger(MybatisGeneratorMainAction.class);

    /**
     * 点击后打开插件主页面
     *
     * @param e
     */
    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        /**
         * 代码生成
         */
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        List<DbTable> dbTables = getSelectedDbTables(e);
        if (dbTables.isEmpty()) {
            logger.error("未选择表, 无法生成代码");
            return;
        }
        ClassGenerateDialogWrapper classGenerateDialogWrapper = new ClassGenerateDialogWrapper(project);
        // 填充默认的选项
        classGenerateDialogWrapper.fillData(project, dbTables);
        classGenerateDialogWrapper.show();
        // 模态窗口选择 OK, 生成相关代码
        if (classGenerateDialogWrapper.getExitCode() == Messages.YES) {
            // 生成代码
            GenerateConfig generateConfig = classGenerateDialogWrapper.determineGenerateConfig();
            if (!generateConfig.checkGenerate()) {
                return;
            }
            generateCode(project, dbTables, generateConfig);
        }
    }

    public void generateCode(Project project, List<DbTable> psiElements, GenerateConfig generateConfig) {
        try {
            // 保存配置, 更新最后一次存储的配置
            TemplatesSettings templatesSettings = TemplatesSettings.getInstance(project);
            TemplateContext templateConfigs = templatesSettings.getTemplateConfigs();
            templateConfigs.setGenerateConfig(generateConfig);
            templateConfigs.setTemplateName(generateConfig.getTemplatesName());
            templateConfigs.setModuleName(generateConfig.getModuleName());
            templatesSettings.setTemplateConfigs(templateConfigs);

            Map<String, DbTable> tableMapping = psiElements.stream().collect(Collectors.toMap(DasObject::getName, a -> a, (a, b) -> a));
            for (TableUIInfo uiInfo : generateConfig.getTableUIInfoList()) {
                String tableName = uiInfo.getTableName();
                DbTable dbTable = tableMapping.get(tableName);
                if (dbTable != null) {
                    // 生成代码
                    GenerateCode.generate(project,
                        generateConfig,
                        templatesSettings.getTemplateSettingMap(),
                        dbTable,
                        uiInfo.getClassName(),
                        uiInfo.getTableName());
                }
            }
            VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
            logger.info("全部代码生成成功, 文件内容已更新. config: {}", generateConfig);
            notify(project, generateConfig);
        } catch (Exception e) {
            logger.error("生成代码出错", e);
        }
    }

    private void notify(Project project, GenerateConfig generateConfig) {
        Notifier.notifyInformation(project,
            TITLE,
            generateConfig.getModuleName() + " generate finished");
    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        List<DbTable> selectedTables = getSelectedDbTables(e);
        e.getPresentation().setEnabledAndVisible(!selectedTables.isEmpty());
    }

    @NotNull
    public static List<DbTable> getSelectedDbTables(@NotNull AnActionEvent e) {
        List<DbTable> dbTables = new ArrayList<>();
        PsiElement[] tableElements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (tableElements != null) {
            for (PsiElement element : tableElements) {
                if (element instanceof DbTable) {
                    dbTables.add((DbTable) element);
                }
            }
        }
        if (dbTables.isEmpty()) {
            PsiElement psiElement = e.getData(LangDataKeys.PSI_ELEMENT);
            if (psiElement == null) {
                psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
            }
            if (psiElement instanceof DbTable) {
                dbTables.add((DbTable) psiElement);
            }
        }
        return dbTables;
    }
}
