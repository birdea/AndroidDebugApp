package com.risewide.bdebugapp.toktok;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "LoginVo")
public class LoginVo {
    @Element
    String primitive;
    @Element
    String result;
    @Element
    String resultMessage;
    @Element(required = false)
    String encEmail;
    @Element(required = false)
    String encEmpId;

    @Element(required = false)
    String deptCd;
    @Element(required = false)
    String deptNm;
    @Element(required = false)
    String email;
    @Element(required = false)
    String empId;
    @Element(required = false)
    String loginId;
    @Element(required = false)
    String userNm;

    @Override
    public String toString() {
        return new StringBuilder()
                .append("primitive:").append(primitive)
                //- COMMON_COMMON_USERINFO
                .append(", encEmail:").append(encEmail)
                .append(", encEmpId:").append(encEmpId)
                //- COMMON_COMMON_EMPINFO
                .append(", deptCd:").append(deptCd)
                .append(", deptNm:").append(deptNm)
                .append(", email:").append(email)
                .append(", empId:").append(empId)
                .append(", loginId:").append(loginId)
                .append(", userNm:").append(userNm)
                //- COMMON
                .append(", result:").append(result)
                .append(", resultMessage:").append(resultMessage)
                //- str
                .toString();
    }
}
