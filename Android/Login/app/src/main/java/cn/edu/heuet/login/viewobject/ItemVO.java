package cn.edu.heuet.login.viewobject;

import java.math.BigDecimal;

public class ItemVO {
    private String id;
    // 名称
    private String title;
    // 描述
    private String description;
    // 图片地址
    private String imgUrl;
    // 价格
    private BigDecimal price;
    // 销量
    private String sales;
    // 库存
    private String stock;

    public ItemVO(String id, String title, String description, String imgUrl, BigDecimal price, String sales, String stock) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imgUrl = imgUrl;
        this.price = price;
        this.sales = sales;
        this.stock = stock;
    }

    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getSales() {
        return sales;
    }

    public String getStock() {
        return stock;
    }
}
