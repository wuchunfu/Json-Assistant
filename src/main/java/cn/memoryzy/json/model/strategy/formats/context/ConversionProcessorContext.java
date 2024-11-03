package cn.memoryzy.json.model.strategy.formats.context;

import cn.hutool.core.util.StrUtil;
import cn.memoryzy.json.enums.TextResolveStatus;
import cn.memoryzy.json.model.formats.DocumentTextInfo;
import cn.memoryzy.json.model.formats.EditorInfo;
import cn.memoryzy.json.model.formats.SelectionInfo;
import cn.memoryzy.json.model.strategy.formats.processor.TomlProcessor;
import cn.memoryzy.json.model.strategy.formats.processor.XmlProcessor;
import cn.memoryzy.json.model.strategy.formats.processor.YamlProcessor;
import cn.memoryzy.json.util.TextTransformUtil;
import com.intellij.openapi.editor.Editor;

/**
 * @author Memory
 * @since 2024/11/2
 */
public class ConversionProcessorContext {

    private AbstractConversionProcessor processor;

    public AbstractConversionProcessor getProcessor() {
        return processor;
    }

    public void setProcessor(AbstractConversionProcessor processor) {
        this.processor = processor;
    }

    /**
     * 代理处理器转换文本
     *
     * @param editorInfo 编辑器信息
     * @return 转换成功的 JSON 文本
     */
    public String convert(EditorInfo editorInfo) {
        SelectionInfo selectionInfo = editorInfo.getSelectionInfo();
        DocumentTextInfo documentTextInfo = editorInfo.getDocumentTextInfo();
        String selectedText = documentTextInfo.getSelectedText();
        String documentText = documentTextInfo.getDocumentText();

        String result;
        if (selectionInfo.isHasSelection()) {
            result = processor.convert(selectedText);
            if (StrUtil.isNotBlank(result)) {
                processor.setTextResolveStatus(TextResolveStatus.SELECTED_SUCCESS);
                return result;
            } else {
                // 如果选择了文本，但是没通过匹配，则跳过，选择下一个处理器
                processor.setTextResolveStatus(TextResolveStatus.RESOLVE_FAILED);
                return null;
            }
        }

        result = processor.convert(documentText);
        processor.setTextResolveStatus(StrUtil.isNotBlank(result)
                ? TextResolveStatus.GLOBAL_SUCCESS
                : TextResolveStatus.RESOLVE_FAILED);

        return result;
    }

    /**
     * 文本是否匹配成功（在转换方法 {@link ConversionProcessorContext#convert(EditorInfo)} 前执行）
     *
     * @param editorInfo 编辑器信息
     * @return 文本匹配成功为 true，反之为 false
     */
    public boolean isMatched(EditorInfo editorInfo) {
        SelectionInfo selectionInfo = editorInfo.getSelectionInfo();
        DocumentTextInfo documentTextInfo = editorInfo.getDocumentTextInfo();
        String selectedText = documentTextInfo.getSelectedText();
        String documentText = documentTextInfo.getDocumentText();

        try {
            if (selectionInfo.isHasSelection()) {
                // 如果选择了文本，通过匹配则返回 true；若没通过匹配，则跳过，选择下一个处理器
                return processor.canConvert(selectedText);
            }

            return processor.canConvert(documentText);
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 尝试不同的策略解析并转换编辑器文本
     *
     * @param context 上下文
     * @param editor  编辑器
     * @return 转换成功的 JSON 文本
     */
    public static String applyProcessors(ConversionProcessorContext context, Editor editor) {
        EditorInfo editorInfo = TextTransformUtil.resolveEditor(editor);
        if (editorInfo == null) return null;
        return applyProcessors(context, getProcessors(editorInfo));
    }

    /**
     * 尝试不同的策略解析并转换编辑器文本
     *
     * @param context    上下文
     * @param processors 处理器
     * @return 转换成功的 JSON 文本
     */
    public static String applyProcessors(ConversionProcessorContext context, AbstractConversionProcessor[] processors) {
        for (AbstractConversionProcessor processor : processors) {
            context.setProcessor(processor);
            String result = context.convert(processor.getEditorInfo());
            if (StrUtil.isNotBlank(result)) {
                return result;
            }
        }
        return null;
    }


    /**
     * 尝试不同的策略解析编辑器文本
     *
     * @param context 上下文
     * @param editor  编辑器
     * @return 解析成功为 true，反之为 false
     */
    public static boolean processMatching(ConversionProcessorContext context, Editor editor) {
        EditorInfo editorInfo = TextTransformUtil.resolveEditor(editor);
        if (editorInfo == null) return false;
        return processMatching(context, getProcessors(editorInfo));
    }

    /**
     * 尝试不同的策略解析编辑器文本
     *
     * @param context    上下文
     * @param processors 处理器
     * @return 解析成功为 true，反之为 false
     */
    public static boolean processMatching(ConversionProcessorContext context, AbstractConversionProcessor[] processors) {
        for (AbstractConversionProcessor processor : processors) {
            context.setProcessor(processor);
            boolean matched = context.isMatched(processor.getEditorInfo());
            if (matched) return true;
        }

        return false;
    }

    /**
     * 获取策略处理器列表
     *
     * @param editorInfo 编辑器信息
     * @return 策略处理器列表
     */
    private static AbstractConversionProcessor[] getProcessors(EditorInfo editorInfo) {
        return new AbstractConversionProcessor[]{
                new XmlProcessor(editorInfo),
                new YamlProcessor(editorInfo),
                new TomlProcessor(editorInfo)
                // 待实现其他的处理器
        };
    }


}
