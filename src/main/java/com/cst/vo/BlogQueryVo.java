package com.cst.vo;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/1/6 3:44 下午
 * @version:
 * @modified By:
 */
public class BlogQueryVo {
            private String title;
            private Long typeId;
            private boolean recommened;

    public boolean isRecommened() {
        return recommened;
    }

    public void setRecommened(boolean recommened) {
        this.recommened = recommened;
    }

    public BlogQueryVo() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }


}
