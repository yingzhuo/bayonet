package com.github.yingzhuo.bayonet.converter;

import java.beans.PropertyEditorSupport;

/**
 * 抽象属性编辑器，模板方法封装 {@link PropertyEditorSupport#setAsText(String)}。
 * <p>子类只需实现 {@link #convert(String)} 方法，专注于字符串到目标类型的转换逻辑。</p>
 *
 * @param <T> 目标类型
 * @author 应卓
 * @since 4.1.1
 */
public abstract class AbstractPropertyEditor<T> extends PropertyEditorSupport {

    @Override
    public final void setAsText(String text) throws IllegalArgumentException {
        try {
            setValue(convert(text));
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException iae) {
                throw iae;
            }
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * 将字符串转换为目标类型。
     *
     * @param text 待转换的字符串，可为 {@code null}
     * @return 转换后的目标类型值
     * @throws Exception 转换失败时抛出
     */
    protected abstract T convert(String text) throws Exception;
}
