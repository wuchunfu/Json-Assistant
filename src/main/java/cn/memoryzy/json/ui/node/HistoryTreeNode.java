package cn.memoryzy.json.ui.node;

import cn.hutool.core.util.StrUtil;
import cn.memoryzy.json.enums.HistoryTreeNodeType;
import cn.memoryzy.json.model.JsonEntry;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Objects;

/**
 * @author Memory
 * @since 2024/11/25
 */
public class HistoryTreeNode extends DefaultMutableTreeNode {

    /**
     * 节点值（只有{@link HistoryTreeNodeType#NODE}类型才有值）
     */
    private JsonEntry value;

    /**
     * 组时间（只有{@link HistoryTreeNodeType#GROUP}类型才有值）
     */
    private String groupTime;

    /**
     * 组大小（只有{@link HistoryTreeNodeType#GROUP}类型才有值）
     */
    private Integer size;

    /**
     * 节点类型
     */
    private HistoryTreeNodeType nodeType;

    // region 构造器和Getter、Setter
    public HistoryTreeNode() {
    }

    public HistoryTreeNode(JsonEntry value, String groupTime, Integer size, HistoryTreeNodeType nodeType) {
        this.value = value;
        this.groupTime = groupTime;
        this.size = size;
        this.nodeType = nodeType;
    }

    public JsonEntry getValue() {
        return value;
    }

    public String getGroupTime() {
        return groupTime;
    }

    public Integer getSize() {
        return size;
    }

    public HistoryTreeNodeType getNodeType() {
        return nodeType;
    }
    // endregion

    @Override
    public String toString() {
        if (Objects.nonNull(nodeType)) {
            if (HistoryTreeNodeType.GROUP == nodeType) {
                return groupTime;
            } else {
                String name = value.getName();
                return StrUtil.isNotBlank(name) ? name : value.getShortText();
            }
        }

        return null;
    }
}
