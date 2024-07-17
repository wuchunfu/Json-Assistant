package cn.memoryzy.json.constant;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * @author Memory
 * @since 2024/7/12
 */
public class PluginConstant {

    public static final String PLUGIN_ID = "cn.memoryzy.json.Json-Assistant";
    public static final String KOTLIN_TRANSIENT = "kotlin.jvm.Transient";
    public static final String LOMBOK_LIB = "org.projectlombok:lombok";
    public static final String JSON_EXAMPLE = " {\"name\": \"王铁柱\", \"age\": 18}";

    public static final String[] COLLECTION_FQN = {
            Iterable.class.getName(),
            Collection.class.getName(),
            List.class.getName(),
            Set.class.getName(),
            ArrayList.class.getName(),
            LinkedList.class.getName()
    };

    public static final String[] BIGDECIMAL_FQN = {BigDecimal.class.getName()};

    public static final String[] DATE_TIME_FQN = {Date.class.getName(), LocalDateTime.class.getName()};

    public static final String[] DATE_FQN = {LocalDate.class.getName()};

    public static final String[] TIME_FQN = {LocalTime.class.getName(), Time.class.getName()};


}
