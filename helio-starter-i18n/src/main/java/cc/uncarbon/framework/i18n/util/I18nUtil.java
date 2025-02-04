package cc.uncarbon.framework.i18n.util;

import cc.uncarbon.framework.core.enums.HelioBaseEnum;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.extra.spring.SpringUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

/**
 * 获取 i18n 资源
 *
 * @author Lion Li
 * @author Uncarbon
 */
@UtilityClass
@Slf4j
public class I18nUtil {

    private final MessageSource MESSAGE_SOURCE = SpringUtil.getBean(MessageSource.class);
    private static final String SLF4J_STYLE_PLACEHOLDER = StrPool.DELIM_START + StrPool.DELIM_END;

    /**
     * 取Spring提供的MessageSource
     */
    public MessageSource getMessageSource() {
        return MESSAGE_SOURCE;
    }

    /**
     * 取默认locale
     * 强国际化SaaS需求，也许可以改造成从特定ThreadLocal或者UserContextHolder中获取
     */
    public Locale getDefaultLocale() {
        return Locale.getDefault();
    }

    /**
     * 根据消息键和参数，获取国际化翻译值；默认使用当前JVM的默认locale
     * 支持模板填充，如: Nickname '{}' is already exist, do you like '{}'?
     *
     * @param code 消息代码
     * @param templateParams 模板填充参数
     * @return null or 国际化翻译值
     */
    public String messageOf(String code, Object... templateParams) {
        return messageOf(getDefaultLocale(), code, templateParams);
    }

    /**
     * 根据消息键和参数，获取国际化翻译值
     * 支持模板填充，如: Nickname '{}' is already exist, do you like '{}'?
     *
     * @param locale locale
     * @param code 消息代码
     * @param templateParams 模板填充参数
     * @return null or 国际化翻译值
     */
    public String messageOf(Locale locale, String code, Object... templateParams) {
        if (code == null) {
            return null;
        }

        try {
            String msg = getMessageSource().getMessage(code, null, locale != null ? locale : getDefaultLocale());
            if (CharSequenceUtil.isEmpty(msg)) {
                return msg;
            }

            if (CharSequenceUtil.contains(msg, SLF4J_STYLE_PLACEHOLDER)) {
                // 使用 hutool 的模板填充
                return CharSequenceUtil.format(msg, templateParams);
            }

            return msg;
        } catch (NoSuchMessageException nsme) {
            // 未找到对应国际化翻译值
        }
        return null;
    }

    /**
     * 根据消息键和参数，获取国际化翻译值；默认使用当前JVM的默认locale
     * 支持模板填充，如: Nickname '{}' is already exist, do you like '{}'?
     *
     * @param code 消息代码
     * @param defaultValue 未找到对应国际化翻译值的情况下，默认返回值
     * @param templateParams 模板填充参数
     * @return null or 国际化翻译值
     */
    public String messageOf(String code, String defaultValue, Object... templateParams) {
        return messageOf(getDefaultLocale(), code, defaultValue, templateParams);
    }

    /**
     * 根据消息键和参数，获取国际化翻译值
     * 支持模板填充，如: Nickname '{}' is already exist, do you like '{}'?
     *
     * @param locale locale
     * @param code 消息代码
     * @param defaultValue 未找到对应国际化翻译值的情况下，默认返回值
     * @param templateParams 模板填充参数
     * @return null or 国际化翻译值
     */
    public String messageOf(Locale locale, String code, String defaultValue, Object... templateParams) {
        String msg = messageOf(locale, code, templateParams);
        return msg == null ? defaultValue : msg;
    }

    /**
     * 根据枚举值和参数，获取国际化翻译值；默认使用当前JVM的默认locale
     * 支持模板填充，如: Nickname '{}' has been existing, do you like '{}'?
     *
     * @param enumField 枚举值
     * @param templateParams 模板填充参数
     * @return null or 国际化翻译值
     */
    public String messageOf(Enum<?> enumField, Object... templateParams) {
        return messageOf(getDefaultLocale(), enumField, templateParams);
    }

    /**
     * 根据枚举值和参数，获取国际化翻译值
     * 支持模板填充，如: Nickname '{}' has been existing, do you like '{}'?
     *
     * @param locale locale
     * @param enumField 枚举值
     * @param templateParams 模板填充参数
     * @return null or 国际化翻译值
     */
    public String messageOf(Locale locale, Enum<?> enumField, Object... templateParams) {
        if (enumField == null) {
            return null;
        }

        String i18nCode;

        // 尝试以较长的 枚举类短名.枚举值name 为 code 尝试获取翻译值
        i18nCode = String.format("%s.%s", enumField.getDeclaringClass().getSimpleName(), enumField.name());
        String i18nMessage = messageOf(locale, i18nCode, templateParams);
        if (CharSequenceUtil.isNotEmpty(i18nMessage)) {
            return i18nMessage;
        }

        // 尝试以 枚举值name 为 code 尝试获取翻译值
        i18nCode = enumField.name();
        i18nMessage = messageOf(locale, i18nCode, templateParams);
        if (CharSequenceUtil.isNotEmpty(i18nMessage)) {
            return i18nMessage;
        }

        // 以上都没找到，兜底：如果是 HelioBaseEnum 的实现，直接返回 label 值
        if (HelioBaseEnum.class.isAssignableFrom(enumField.getDeclaringClass())) {
            return ((HelioBaseEnum<?>) enumField).getLabel();
        }

        // 最终兜底：返回枚举值 name
        return enumField.name();
    }
}
