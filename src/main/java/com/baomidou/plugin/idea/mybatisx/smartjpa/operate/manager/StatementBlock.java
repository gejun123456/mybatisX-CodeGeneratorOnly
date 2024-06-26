package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager;

import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.AreaSequence;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomAreaAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TypeDescriptor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * The type Statement block.
 */
@Getter
public class StatementBlock {

    /**
     * 标签名称
     * -- GETTER --
     *  Gets tag name.
     *
     * @return the tag name

     */
    private String tagName;
    /**
     * 结果集区域
     * -- GETTER --
     *  Gets result appender factory.
     *
     * @return the result appender factory

     */
    private SyntaxAppenderFactory resultAppenderFactory;
    /**
     * 条件区域
     * -- GETTER --
     *  Gets condition appender factory.
     *
     * @return the condition appender factory

     */
    private SyntaxAppenderFactory conditionAppenderFactory;
    /**
     * 排序区域
     * -- GETTER --
     *  Gets sort appender factory.
     *
     * @return the sort appender factory

     */
    private SyntaxAppenderFactory sortAppenderFactory;
    /**
     * 返回值类型
     * -- GETTER --
     *  Gets return descriptor.
     *
     * @return the return descriptor

     */
    private TypeDescriptor returnDescriptor;

    /**
     * Sets result appender factory.
     *
     * @param resultAppenderFactory the result appender factory
     */
    public void setResultAppenderFactory(SyntaxAppenderFactory resultAppenderFactory) {
        this.resultAppenderFactory = resultAppenderFactory;
    }

    /**
     * Sets condition appender factory.
     *
     * @param conditionAppenderFactory the condition appender factory
     */
    public void setConditionAppenderFactory(SyntaxAppenderFactory conditionAppenderFactory) {
        this.conditionAppenderFactory = conditionAppenderFactory;
    }

    /**
     * Sets sort appender factory.
     *
     * @param sortAppenderFactory the sort appender factory
     */
    public void setSortAppenderFactory(SyntaxAppenderFactory sortAppenderFactory) {
        this.sortAppenderFactory = sortAppenderFactory;
    }

    /**
     * Sets tag name.
     *
     * @param tagName the tag name
     */
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }


    /**
     * Gets syntax appender factory by str.
     *
     * @param text the text
     * @return the syntax appender factory by str
     */
    public SyntaxAppenderFactory getSyntaxAppenderFactoryByStr(String text) {
        if (existCurrentArea(getResultAppenderFactory(), text)) {
            return getResultAppenderFactory();
        }
        if (existCurrentArea(getConditionAppenderFactory(), text)) {
            return getConditionAppenderFactory();
        }
        if (existCurrentArea(getSortAppenderFactory(), text)) {
            return getSortAppenderFactory();
        }
        return getResultAppenderFactory();
    }

    private boolean existCurrentArea(SyntaxAppenderFactory appenderFactory, String text) {
        if (appenderFactory == null) {
            return false;
        }
        return text.equals(appenderFactory.getTipText());
    }

    /**
     * Sets return wrapper.
     *
     * @param typeDescriptor the type descriptor
     */
    public void setReturnWrapper(TypeDescriptor typeDescriptor) {
        this.returnDescriptor = typeDescriptor;
    }

    /**
     * Find priority linked list.
     *
     * @param stringLengthComparator the string length comparator
     * @param splitStr               the split str
     * @return the linked list
     */
    public LinkedList<SyntaxAppender> findPriority(Comparator<SyntaxAppender> stringLengthComparator,
                                                   String splitStr) {
        if (StringUtils.isNoneBlank(splitStr) &&
            !(getTagName().startsWith(splitStr) || splitStr.startsWith(getTagName()))) {
            return new LinkedList<>();
        }
        String replaceStr = splitStr;
        LinkedList<SyntaxAppender> syntaxAppenderList = new LinkedList<>();
        AreaSequence currentArea = AreaSequence.RESULT;
        // 找到一个合适的前缀
        while (replaceStr.length() > 0) {
            PriorityQueue<SyntaxAppender> priorityQueue = new PriorityQueue<>(stringLengthComparator);
            if (currentArea.getSequence() <= AreaSequence.RESULT.getSequence()) {
                resultAppenderFactory.findPriority(priorityQueue, syntaxAppenderList, replaceStr);
            }
            if (conditionAppenderFactory != null && currentArea.getSequence() <= AreaSequence.CONDITION.getSequence()) {
                conditionAppenderFactory.findPriority(priorityQueue, syntaxAppenderList, replaceStr);
            }
            if (sortAppenderFactory != null && currentArea.getSequence() <= AreaSequence.SORT.getSequence()) {
                sortAppenderFactory.findPriority(priorityQueue, syntaxAppenderList, replaceStr);
            }
            SyntaxAppender priorityAppender = priorityQueue.peek();
            if (priorityAppender == null) {
                break;
            }
            syntaxAppenderList.add(priorityAppender);
            if (priorityAppender.getAreaSequence() == AreaSequence.AREA) {
                currentArea = ((CustomAreaAppender) priorityAppender).getChildAreaSequence();
            } else {
                currentArea = priorityAppender.getAreaSequence();
            }
            replaceStr = replaceStr.substring(priorityAppender.getText().length());
        }
        return syntaxAppenderList;
    }
}
