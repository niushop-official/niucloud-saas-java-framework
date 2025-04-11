package com.niu.core.service.core.pay.vo;

import com.niu.core.enums.pay.TransferStatusEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class TransferQueryVo  implements Serializable {

    private static final long serialVersionUID = 1L;

    private String transferNo;

    private TransferStatusEnum transferSatus;

    private Object original;

    private String failReason;
}
