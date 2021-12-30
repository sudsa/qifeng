package cn.stylefeng.guns.modular.vsshop.model;

/**
 * Created by jianyinlin on 2019/5/29
 */

public enum UserType {
   // 1;时间类型，2次数类型；3；积分类型
    DateType("1", "时间类型"),
    CountType("2", "次数类型"),
    CoinType("3", "积分类型");

    private String value;
    private String name;

    UserType(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static UserType getEnum(String value) {
        if (value == null) {
            return null;
        }

        final UserType[] enumConstants = UserType.class.getEnumConstants();
        for (UserType enumConstant : enumConstants) {
            if (value.equals(enumConstant.getValue())) {
                return enumConstant;
            }
        }
        return null;
    }

    public static String getName(String value) {
        UserType enumConstant = getEnum(value);
        if(enumConstant != null)
            return enumConstant.getName();
        return null;
    }
}
