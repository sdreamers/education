package com.sixing.base.domain.base;

public class BaseVO {

    // 是否使用悲观锁
    private Boolean forUpdate;
    // 根据哪个字段进行排序
    private String sortBy;
    // 排序方向，案例：ASC、DESC
    private String direction;

    private String[] sortAndDirectionArr;

    public Boolean getForUpdate() {
        return forUpdate;
    }

    public void setForUpdate(Boolean forUpdate) {
        this.forUpdate = forUpdate;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String[] getSortAndDirectionArr() {
        return sortAndDirectionArr;
    }

    public void setSortAndDirectionArr(String[] sortAndDirectionArr) {
        this.sortAndDirectionArr = sortAndDirectionArr;
    }
}
