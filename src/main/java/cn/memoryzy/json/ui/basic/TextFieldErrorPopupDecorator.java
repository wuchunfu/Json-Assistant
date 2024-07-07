package cn.memoryzy.json.ui.basic;

import com.intellij.openapi.ui.ComponentValidator;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.AncestorListenerAdapter;
import com.intellij.ui.JBColor;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.popup.AbstractPopup;
import com.intellij.ui.scale.JBUIScale;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.AncestorEvent;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * @author Memory
 * @since 2024/1/26
 */
public class TextFieldErrorPopupDecorator {

    private final JComponent myTextField;
    private RelativePoint myErrorShowPoint;
    private AbstractPopup myErrorPopup;
    private Dimension hintSize;

    public TextFieldErrorPopupDecorator(JRootPane rootPane, JComponent myTextField) {
        this.myTextField = myTextField;
        // 初始化监听事件
        initElement(rootPane, myTextField);
        myErrorShowPoint = new RelativePoint(myTextField, new Point(0, myTextField.getHeight()));
    }


    private void initElement(JRootPane rootPane, JComponent myTextField) {
        // 注册组件移动事件
        rootPane.addAncestorListener(new AncestorListenerAdapter() {
            /**
             * 弹窗移动时事件
             */
            @Override
            public void ancestorMoved(AncestorEvent event) {
                // 弹出提示-跟随移动
                if (myTextField != null) {
                    if (myErrorPopup != null) {
                        // 永远在输入框上方的位置
                        Insets insets = myTextField.getInsets();
                        Point point = new Point(0, insets.top - JBUIScale.scale(6) - hintSize.height);
                        RelativePoint relativePoint = new RelativePoint(myTextField, point);
                        myErrorPopup.setLocation(relativePoint);
                    }
                }
            }
        });

        rootPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                disposePopup();
            }
        });
    }

    public void setError(String error) {
        // 这行的作用是给文本框外部变为红色
        if (myTextField instanceof JList) {
            Border lineBorder = BorderFactory.createLineBorder(JBColor.red, 2);
            myTextField.setBorder(lineBorder);
        } else {
            myTextField.putClientProperty("JComponent.outline", error != null ? "error" : null);
        }

        if (myErrorPopup != null && !myErrorPopup.isDisposed()) Disposer.dispose(myErrorPopup);
        if (error == null) return;

        // 聚焦文本框（代替复合边框的方案）
        myTextField.requestFocusInWindow();

        ComponentPopupBuilder popupBuilder = ComponentValidator.createPopupBuilder(new ValidationInfo(error, myTextField), errorHint -> {
                    Insets insets = myTextField.getInsets();
                    hintSize = errorHint.getPreferredSize();
                    int y = insets.top - JBUIScale.scale(6) - hintSize.height;
                    Point point = new Point(2, y);
                    myErrorShowPoint = new RelativePoint(myTextField, point);
                }).setCancelOnWindowDeactivation(false)
                .setCancelOnClickOutside(true)
                .setRequestFocus(true)
                .addUserData("SIMPLE_WINDOW");

        myErrorPopup = (AbstractPopup) popupBuilder.createPopup();
        myErrorPopup.show(myErrorShowPoint);
    }


    private void disposePopup() {
        if (myErrorPopup != null && !myErrorPopup.isDisposed()) Disposer.dispose(myErrorPopup);
    }

}
