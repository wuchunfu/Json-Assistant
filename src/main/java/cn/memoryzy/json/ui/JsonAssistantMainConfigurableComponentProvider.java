package cn.memoryzy.json.ui;

import cn.memoryzy.json.bundle.JsonAssistantBundle;
import cn.memoryzy.json.enums.ColorScheme;
import cn.memoryzy.json.enums.HistoryViewType;
import cn.memoryzy.json.enums.TreeDisplayMode;
import cn.memoryzy.json.service.persistent.JsonAssistantPersistentState;
import cn.memoryzy.json.service.persistent.state.*;
import cn.memoryzy.json.ui.dialog.SupportDialog;
import cn.memoryzy.json.ui.icon.CircleIcon;
import cn.memoryzy.json.util.PlatformUtil;
import cn.memoryzy.json.util.UIManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.ColorPicker;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.ActionLink;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.util.ui.JBEmptyBorder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import icons.JsonAssistantIcons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * @author Memory
 * @since 2024/10/31
 */
public class JsonAssistantMainConfigurableComponentProvider {
    // region 组件
    private JPanel rootPanel;
    private TitledSeparator attributeSerializationLabel;
    private JBCheckBox includeRandomValuesCb;
    private JBLabel includeRandomValuesDesc;
    private JBCheckBox fastJsonCb;
    private JBLabel fastJsonDesc;
    private JBCheckBox jacksonCb;
    private JBLabel jacksonDesc;
    private TitledSeparator windowBehaviorLabel;
    private JBCheckBox displayLineNumbersCb;
    private JBCheckBox foldingOutlineCb;
    private ActionLink donateLink;
    private JBCheckBox recognizeOtherFormatsCb;
    private JBLabel recognizeOtherFormatsDesc;
    private JBCheckBox xmlFormatsCb;
    private JBCheckBox yamlFormatsCb;
    private JBCheckBox tomlFormatsCb;
    private JBCheckBox urlParamFormatsCb;
    private JPanel formatCbPanel;
    private JBLabel backgroundColorTitle;
    private ComboBox<ColorScheme> backgroundColorBox;
    private TitledSeparator windowAppearanceLabel;
    private JPanel backgroundColorPanel;
    private JBLabel backgroundColorDesc;
    private TitledSeparator historyLabel;
    private JBLabel historyStyleTitle;
    private JBCheckBox recordHistory;
    private ComboBox<HistoryViewType> historyStyleBox;
    private TitledSeparator generalLabel;
    private JBLabel treeDisplayModeTitle;
    private JBLabel treeDisplayModeDesc;
    private ComboBox<TreeDisplayMode> treeDisplayModeBox;
    private JBCheckBox promptBeforeImportCb;
    private JBLabel promptBeforeImportDesc;
    private JBLabel autoStoreHistoryTitle;
    private JBRadioButton autoStoreRb;
    private JBRadioButton manualStoreRb;
    private JBLabel autoStoreHistoryDesc;
    // endregion

    // 区分亮暗，防止配置界面还存在时，主题被切换
    private Color selectedLightColor;
    private Color selectedDarkColor;

    /**
     * 标志变量，用于标识是否处于初始加载状态
     */
    private boolean isLoading = false;
    private final JsonAssistantPersistentState persistentState = JsonAssistantPersistentState.getInstance();
    private final boolean isIdea = PlatformUtil.isIdea();

    public JPanel createComponent() {
        configureGeneralComponents();
        configureAttributeSerializationComponents();
        configureToolWindowBehaviorComponents();
        configureToolWindowAppearanceComponents();
        configureHistoryComponents();
        configureDonateLinkComponents();
        // 初始化
        reset();
        return rootPanel;
    }

    /**
     * 常规
     */
    private void configureGeneralComponents() {
        generalLabel.setText(JsonAssistantBundle.messageOnSystem("setting.component.general.text"));
        treeDisplayModeTitle.setText(JsonAssistantBundle.messageOnSystem("setting.component.tree.display.mode.text"));
        UIManager.setHelpLabel(treeDisplayModeDesc, JsonAssistantBundle.messageOnSystem("setting.component.tree.display.mode.desc"));

        for (TreeDisplayMode value : TreeDisplayMode.values()) {
            treeDisplayModeBox.addItem(value);
        }
    }

    /**
     * 属性序列化
     */
    private void configureAttributeSerializationComponents() {
        attributeSerializationLabel.setText(JsonAssistantBundle.messageOnSystem("setting.component.attribute.serialization.text"));

        includeRandomValuesCb.setText(JsonAssistantBundle.messageOnSystem("setting.component.random.value.text"));
        UIManager.setCommentLabel(includeRandomValuesDesc, includeRandomValuesCb, JsonAssistantBundle.messageOnSystem("setting.component.random.value.desc"));

        fastJsonCb.setText(JsonAssistantBundle.messageOnSystem("setting.component.fastjson.text"));
        UIManager.setCommentLabel(fastJsonDesc, fastJsonCb, JsonAssistantBundle.messageOnSystem("setting.component.fastjson.desc"));

        jacksonCb.setText(JsonAssistantBundle.messageOnSystem("setting.component.jackson.text"));
        UIManager.setCommentLabel(jacksonDesc, jacksonCb, JsonAssistantBundle.messageOnSystem("setting.component.jackson.desc"));
    }

    /**
     * 窗口行为
     */
    private void configureToolWindowBehaviorComponents() {
        windowBehaviorLabel.setText(JsonAssistantBundle.messageOnSystem("setting.component.window.behavior.text"));

        recognizeOtherFormatsCb.setText(JsonAssistantBundle.messageOnSystem("setting.component.recognize.other.formats.text"));
        UIManager.setHelpLabel(recognizeOtherFormatsDesc, JsonAssistantBundle.messageOnSystem("setting.component.recognize.other.formats.desc"));

        xmlFormatsCb.setText("XML");
        yamlFormatsCb.setText("YAML");
        tomlFormatsCb.setText("TOML");
        urlParamFormatsCb.setText("URL Param");

        int left = UIUtil.getCheckBoxTextHorizontalOffset(recognizeOtherFormatsCb);
        formatCbPanel.setBorder(new JBEmptyBorder(JBUI.insets(1, left, 4, 0)));

        // 识别剪贴板数据后，需要确认才能真正导入到编辑器中
        promptBeforeImportCb.setText(JsonAssistantBundle.messageOnSystem("setting.component.import.prompt.text"));
        UIManager.setCommentLabel(promptBeforeImportDesc, promptBeforeImportCb, JsonAssistantBundle.messageOnSystem("setting.component.import.prompt.desc"));

        recognizeOtherFormatsCb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                UIManager.controlEnableCheckBox(xmlFormatsCb, true);
                UIManager.controlEnableCheckBox(yamlFormatsCb, true);
                UIManager.controlEnableCheckBox(tomlFormatsCb, true);
                UIManager.controlEnableCheckBox(urlParamFormatsCb, true);
                UIManager.controlEnableCheckBox(promptBeforeImportCb, true);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                UIManager.controlEnableCheckBox(xmlFormatsCb, false);
                UIManager.controlEnableCheckBox(yamlFormatsCb, false);
                UIManager.controlEnableCheckBox(tomlFormatsCb, false);
                UIManager.controlEnableCheckBox(urlParamFormatsCb, false);
                UIManager.controlEnableCheckBox(promptBeforeImportCb, false);
            }
        });
    }

    /**
     * 窗口外观
     */
    private void configureToolWindowAppearanceComponents() {
        windowAppearanceLabel.setText(JsonAssistantBundle.messageOnSystem("setting.component.window.appearance.text"));
        displayLineNumbersCb.setText(JsonAssistantBundle.messageOnSystem("setting.component.display.lines.text"));
        foldingOutlineCb.setText(JsonAssistantBundle.messageOnSystem("setting.component.folding.outline.text"));
        // 单独设定背景色
        configureBackGroundColorComponents();
    }

    private void configureBackGroundColorComponents() {
        if (isIdea) {
            backgroundColorTitle.setText(JsonAssistantBundle.messageOnSystem("setting.component.background.color.text"));
            for (ColorScheme value : ColorScheme.values()) {
                backgroundColorBox.addItem(value);
            }

            UIManager.setHelpLabel(backgroundColorDesc, JsonAssistantBundle.messageOnSystem("setting.component.background.color.desc"));

            // 当有焦点时，表示内部活动完毕，此时才允许用户选择颜色
            backgroundColorBox.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    isLoading = false;
                }
            });

            backgroundColorBox.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ColorScheme item = backgroundColorBox.getItem();
                    if (ColorScheme.Custom.equals(item) && !isLoading) {
                        backgroundColorBox.hidePopup();
                        boolean darkTheme = UIUtil.isUnderDarcula();
                        EditorAppearanceState editorAppearanceState = persistentState.editorAppearanceState;
                        Color preselectedColor = darkTheme ? editorAppearanceState.customDarkcolor : editorAppearanceState.customLightColor;
                        String title = darkTheme ? JsonAssistantBundle.messageOnSystem("dialog.choose.dark.color.title") : JsonAssistantBundle.messageOnSystem("dialog.choose.light.color.title");

                        Color selectedColor = ColorPicker.showDialog(
                                backgroundColorBox, title,
                                preselectedColor, true, null, true);

                        if (null != selectedColor) {
                            if (darkTheme) {
                                selectedDarkColor = selectedColor;
                            } else {
                                selectedLightColor = selectedColor;
                            }

                            UIManager.repaintComponent(backgroundColorBox);
                        }
                    }
                }
            });

            backgroundColorBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    // 调用父类方法
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    Color color = getColor((ColorScheme) value);

                    setIcon(Objects.isNull(color)
                            // 创建一个空白图标
                            ? new ImageIcon(new BufferedImage(14, 14, BufferedImage.TYPE_INT_ARGB))
                            // 创建圆形图标
                            : new CircleIcon(14, color));

                    return this;
                }

                @Nullable
                private Color getColor(ColorScheme colorScheme) {
                    Color color = colorScheme.getColor();
                    boolean darkTheme = UIUtil.isUnderDarcula();
                    Color selectColor = darkTheme ? selectedDarkColor : selectedLightColor;

                    // 实现自定义中，颜色选择后，颜色图标跟随变化
                    // 选择的颜色默认是null，当选择后才会被赋值
                    // 只要与持久化中的颜色不同，那么表示选中了新颜色，即切换颜色图标
                    if (ColorScheme.Custom.equals(colorScheme)
                            && Objects.nonNull(selectColor)
                            && Objects.nonNull(color)
                            && !Objects.equals(selectColor, color)) {
                        color = selectColor;
                    }

                    return color;
                }
            });
        } else {
            backgroundColorPanel.setVisible(false);
        }
    }

    /**
     * 历史记录
     */
    private void configureHistoryComponents() {
        historyLabel.setText(JsonAssistantBundle.messageOnSystem("setting.component.history.text"));
        recordHistory.setText(JsonAssistantBundle.messageOnSystem("setting.component.history.record.text"));
        historyStyleTitle.setText(JsonAssistantBundle.messageOnSystem("setting.component.history.style.text"));
        for (HistoryViewType value : HistoryViewType.values()) {
            historyStyleBox.addItem(value);
        }

        autoStoreHistoryTitle.setText(JsonAssistantBundle.messageOnSystem("setting.component.history.auto.store.text"));
        autoStoreRb.setText(JsonAssistantBundle.messageOnSystem("setting.component.history.auto.text"));
        manualStoreRb.setText(JsonAssistantBundle.messageOnSystem("setting.component.history.manual.text"));
        UIManager.setHelpLabel(autoStoreHistoryDesc, JsonAssistantBundle.messageOnSystem("setting.component.history.auto.store.desc"));

        ButtonGroup group = new ButtonGroup();
        group.add(autoStoreRb);
        group.add(manualStoreRb);

        recordHistory.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                UIManager.controlEnableRadioButton(autoStoreRb, true);
                UIManager.controlEnableRadioButton(manualStoreRb, true);
                if (!historyStyleBox.isEnabled()) {
                    historyStyleBox.setEnabled(true);
                }
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                UIManager.controlEnableRadioButton(autoStoreRb, false);
                UIManager.controlEnableRadioButton(manualStoreRb, false);
                if (historyStyleBox.isEnabled()) {
                    historyStyleBox.setEnabled(false);
                }
            }
        });
    }

    /**
     * 支持/捐赠
     */
    private void configureDonateLinkComponents() {
        donateLink.setIcon(JsonAssistantIcons.DONATE);
        donateLink.setText(JsonAssistantBundle.messageOnSystem("action.donate.welcome.text"));
        donateLink.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SupportDialog().show();
            }
        });
    }


    // ----------------------------------------------------------------------------------- //

    public void reset() {
        // 属性序列化
        AttributeSerializationState attributeSerializationState = persistentState.attributeSerializationState;
        includeRandomValuesCb.setSelected(attributeSerializationState.includeRandomValues);
        fastJsonCb.setSelected(attributeSerializationState.recognitionFastJsonAnnotation);
        jacksonCb.setSelected(attributeSerializationState.recognitionJacksonAnnotation);

        // 行为
        EditorBehaviorState editorBehaviorState = persistentState.editorBehaviorState;
        // 控制全部CheckBox
        boolean recognizeOtherFormats = editorBehaviorState.recognizeOtherFormats;
        recognizeOtherFormatsCb.setSelected(recognizeOtherFormats);
        xmlFormatsCb.setSelected(editorBehaviorState.recognizeXmlFormat);
        yamlFormatsCb.setSelected(editorBehaviorState.recognizeYamlFormat);
        tomlFormatsCb.setSelected(editorBehaviorState.recognizeTomlFormat);
        urlParamFormatsCb.setSelected(editorBehaviorState.recognizeUrlParamFormat);
        promptBeforeImportCb.setSelected(editorBehaviorState.promptBeforeImport);

        // 外观
        EditorAppearanceState editorAppearanceState = persistentState.editorAppearanceState;
        if (isIdea) {
            resetBackgroundColorItem(editorAppearanceState);
        }

        displayLineNumbersCb.setSelected(editorAppearanceState.displayLineNumbers);
        foldingOutlineCb.setSelected(editorAppearanceState.foldingOutline);

        // 历史记录
        HistoryState historyState = persistentState.historyState;
        boolean switchHistory = historyState.switchHistory;
        recordHistory.setSelected(switchHistory);
        historyStyleBox.setItem(historyState.historyViewType);
        if (historyState.autoStore) {
            autoStoreRb.setSelected(true);
        } else {
            manualStoreRb.setSelected(true);
        }

        if (switchHistory) {
            UIManager.controlEnableRadioButton(autoStoreRb, true);
            UIManager.controlEnableRadioButton(manualStoreRb, true);
            if (!historyStyleBox.isEnabled()) {
                historyStyleBox.setEnabled(true);
            }
        } else {
            UIManager.controlEnableRadioButton(autoStoreRb, false);
            UIManager.controlEnableRadioButton(manualStoreRb, false);
            if (historyStyleBox.isEnabled()) {
                historyStyleBox.setEnabled(false);
            }
        }

        if (recognizeOtherFormats) {
            UIManager.controlEnableCheckBox(xmlFormatsCb, true);
            UIManager.controlEnableCheckBox(yamlFormatsCb, true);
            UIManager.controlEnableCheckBox(tomlFormatsCb, true);
            UIManager.controlEnableCheckBox(urlParamFormatsCb, true);
            UIManager.controlEnableCheckBox(promptBeforeImportCb, true);
        } else {
            UIManager.controlEnableCheckBox(xmlFormatsCb, false);
            UIManager.controlEnableCheckBox(yamlFormatsCb, false);
            UIManager.controlEnableCheckBox(tomlFormatsCb, false);
            UIManager.controlEnableCheckBox(urlParamFormatsCb, false);
            UIManager.controlEnableCheckBox(promptBeforeImportCb, false);
        }

        if (isIdea) {
            UIManager.repaintComponent(backgroundColorBox);
        }

        // 常规
        GeneralState generalState = persistentState.generalState;
        treeDisplayModeBox.setItem(generalState.treeDisplayMode);
    }

    private void resetBackgroundColorItem(EditorAppearanceState editorAppearanceState) {
        // 在初始化组件、Reset时，不需要弹出颜色选择窗
        isLoading = true;
        backgroundColorBox.setItem(editorAppearanceState.colorScheme);
        selectedLightColor = editorAppearanceState.customLightColor;
        selectedDarkColor = editorAppearanceState.customDarkcolor;
        isLoading = false;
    }


    public boolean isModified() {
        // 属性序列化
        AttributeSerializationState attributeSerializationState = persistentState.attributeSerializationState;
        boolean oldIncludeRandomValues = attributeSerializationState.includeRandomValues;
        boolean oldRecognitionFastJsonAnnotation = attributeSerializationState.recognitionFastJsonAnnotation;
        boolean oldRecognitionJacksonAnnotation = attributeSerializationState.recognitionJacksonAnnotation;

        // 行为
        EditorBehaviorState editorBehaviorState = persistentState.editorBehaviorState;
        boolean oldRecognizeOtherFormats = editorBehaviorState.recognizeOtherFormats;
        boolean oldRecognizeXmlFormat = editorBehaviorState.recognizeXmlFormat;
        boolean oldRecognizeYamlFormat = editorBehaviorState.recognizeYamlFormat;
        boolean oldRecognizeTomlFormat = editorBehaviorState.recognizeTomlFormat;
        boolean oldRecognizeUrlParamFormat = editorBehaviorState.recognizeUrlParamFormat;
        boolean oldPromptBeforeImport = editorBehaviorState.promptBeforeImport;

        // 外观
        EditorAppearanceState editorAppearanceState = persistentState.editorAppearanceState;
        boolean oldDisplayLineNumbers = editorAppearanceState.displayLineNumbers;
        boolean oldFoldingOutline = editorAppearanceState.foldingOutline;
        ColorScheme oldColorScheme = editorAppearanceState.colorScheme;
        // 比较自定义颜色是否存在变更
        Color oldDarkcolor = editorAppearanceState.customDarkcolor;
        Color oldLightColor = editorAppearanceState.customLightColor;

        // 历史记录
        HistoryState historyState = persistentState.historyState;
        boolean oldSwitchHistory = historyState.switchHistory;
        boolean oldAutoStore = historyState.autoStore;
        HistoryViewType oldHistoryViewType = historyState.historyViewType;

        // 常规
        GeneralState generalState = persistentState.generalState;
        TreeDisplayMode oldTreeDisplayMode = generalState.treeDisplayMode;

        // ----------------------------------------------------------------------

        // 属性序列化
        boolean newIncludeRandomValues = includeRandomValuesCb.isSelected();
        boolean newRecognitionFastJsonAnnotation = fastJsonCb.isSelected();
        boolean newRecognitionJacksonAnnotation = jacksonCb.isSelected();

        // 外观
        ColorScheme newColorScheme = backgroundColorBox.getItem();
        boolean newDisplayLineNumbers = displayLineNumbersCb.isSelected();
        boolean newFoldingOutline = foldingOutlineCb.isSelected();

        // 解析
        boolean newRecognizeOtherFormats = recognizeOtherFormatsCb.isSelected();
        boolean newRecognizeXmlFormat = xmlFormatsCb.isSelected();
        boolean newRecognizeYamlFormat = yamlFormatsCb.isSelected();
        boolean newRecognizeTomlFormat = tomlFormatsCb.isSelected();
        boolean newRecognizeUrlParamFormat = urlParamFormatsCb.isSelected();
        boolean newPromptBeforeImport = promptBeforeImportCb.isSelected();

        // 历史记录
        boolean newSwitchHistory = recordHistory.isSelected();
        boolean newAutoStore = autoStoreRb.isSelected();
        HistoryViewType newHistoryViewType = historyStyleBox.getItem();

        // 常规
        TreeDisplayMode newTreeDisplayMode = treeDisplayModeBox.getItem();

        // 比较是否更改
        return !Objects.equals(oldIncludeRandomValues, newIncludeRandomValues)
                || !Objects.equals(oldRecognitionFastJsonAnnotation, newRecognitionFastJsonAnnotation)
                || !Objects.equals(oldRecognitionJacksonAnnotation, newRecognitionJacksonAnnotation)

                || (isIdea && !Objects.equals(oldColorScheme, newColorScheme))
                || (isIdea && (ColorScheme.Custom.equals(newColorScheme)
                // 自定义颜色比较
                && !Objects.equals(oldLightColor, selectedLightColor) || !Objects.equals(oldDarkcolor, selectedDarkColor)))

                || !Objects.equals(oldDisplayLineNumbers, newDisplayLineNumbers)
                || !Objects.equals(oldFoldingOutline, newFoldingOutline)

                || !Objects.equals(oldRecognizeOtherFormats, newRecognizeOtherFormats)
                || !Objects.equals(oldRecognizeXmlFormat, newRecognizeXmlFormat)
                || !Objects.equals(oldRecognizeYamlFormat, newRecognizeYamlFormat)
                || !Objects.equals(oldRecognizeTomlFormat, newRecognizeTomlFormat)
                || !Objects.equals(oldRecognizeUrlParamFormat, newRecognizeUrlParamFormat)
                || !Objects.equals(oldPromptBeforeImport, newPromptBeforeImport)
                || !Objects.equals(oldSwitchHistory, newSwitchHistory)
                || !Objects.equals(oldAutoStore, newAutoStore)
                || !Objects.equals(oldHistoryViewType, newHistoryViewType)
                || !Objects.equals(oldTreeDisplayMode, newTreeDisplayMode)

                ;
    }

    public void apply() {
        // 属性序列化
        AttributeSerializationState attributeSerializationState = persistentState.attributeSerializationState;
        attributeSerializationState.includeRandomValues = includeRandomValuesCb.isSelected();
        attributeSerializationState.recognitionFastJsonAnnotation = fastJsonCb.isSelected();
        attributeSerializationState.recognitionJacksonAnnotation = jacksonCb.isSelected();

        // 行为
        EditorBehaviorState editorBehaviorState = persistentState.editorBehaviorState;
        editorBehaviorState.recognizeOtherFormats = recognizeOtherFormatsCb.isSelected();
        editorBehaviorState.recognizeXmlFormat = xmlFormatsCb.isSelected();
        editorBehaviorState.recognizeYamlFormat = yamlFormatsCb.isSelected();
        editorBehaviorState.recognizeTomlFormat = tomlFormatsCb.isSelected();
        editorBehaviorState.recognizeUrlParamFormat = urlParamFormatsCb.isSelected();
        editorBehaviorState.promptBeforeImport = promptBeforeImportCb.isSelected();

        // 历史记录
        HistoryState historyState = persistentState.historyState;
        historyState.switchHistory = recordHistory.isSelected();
        historyState.autoStore = autoStoreRb.isSelected();
        historyState.historyViewType = historyStyleBox.getItem();

        // 外观
        EditorAppearanceState editorAppearanceState = persistentState.editorAppearanceState;
        editorAppearanceState.displayLineNumbers = displayLineNumbersCb.isSelected();
        editorAppearanceState.foldingOutline = foldingOutlineCb.isSelected();

        if (isIdea) {
            ColorScheme selectedScheme = backgroundColorBox.getItem();
            editorAppearanceState.colorScheme = selectedScheme;

            // 如果选择的是Custom，那么将选择的颜色赋值给customColor，这个color可以暂时缓存起来
            if (ColorScheme.Custom.equals(selectedScheme)) {
                if (Objects.nonNull(selectedDarkColor)) {
                    editorAppearanceState.customDarkcolor = selectedDarkColor;
                }

                if (Objects.nonNull(selectedLightColor)) {
                    editorAppearanceState.customLightColor = selectedLightColor;
                }
            }
        }

        // 常规
        GeneralState generalState = persistentState.generalState;
        generalState.treeDisplayMode = treeDisplayModeBox.getItem();
    }

}
