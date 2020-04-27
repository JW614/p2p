package com.bjpowernode.p2p.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName:PaginationVO
 * Package:com.bjpowernode.p2p.model.vo
 * Description:
 *
 * @date:2020/4/27 11:47
 * @author:动力节点
 */
@Data
public class PaginationVO<T> implements Serializable {

    private Long total;

    private List<T> dataList;

}
