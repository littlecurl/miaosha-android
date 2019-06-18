package edu.heuet.android.logindemo.service.impl;

import edu.heuet.android.logindemo.dao.ItemDOMapper;
import edu.heuet.android.logindemo.dao.ItemStockDOMapper;
import edu.heuet.android.logindemo.dataobject.ItemDO;
import edu.heuet.android.logindemo.dataobject.ItemStockDO;
import edu.heuet.android.logindemo.error.BusinessException;
import edu.heuet.android.logindemo.error.EmBusinessError;
import edu.heuet.android.logindemo.service.ItemService;
import edu.heuet.android.logindemo.service.model.ItemModel;
import edu.heuet.android.logindemo.validator.ValidationResult;
import edu.heuet.android.logindemo.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private ItemDOMapper itemDOMapper;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        // 校验入参
        ValidationResult result = validator.validate(itemModel);
        if (result.isHasError()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }
        // model 转为 dataobject
        ItemDO itemDO = this.convertItemDOFromItemModel(itemModel);
        // 写入数据库
        itemDOMapper.insertSelective(itemDO);
        // 这一步不能少
        itemModel.setId(itemDO.getId());
        // model 转为 dataobject
        ItemStockDO itemStockDO = this.convertItemStockDOFromItemModel(itemModel);
        // 写入数据库
        itemStockDOMapper.insertSelective(itemStockDO);

        return this.getItemById(itemModel.getId());
    }

    private ItemDO convertItemDOFromItemModel(ItemModel itemModel){
        if (itemModel == null){
            return null;
        }

        ItemDO itemDO = new ItemDO();

        BeanUtils.copyProperties(itemModel, itemDO);
        itemDO.setPrice(itemModel.getPrice().doubleValue());

        return itemDO;
    }

    private ItemStockDO convertItemStockDOFromItemModel(ItemModel itemModel){
        if (itemModel == null){
            return  null;
        }

        ItemStockDO itemStockDO = new ItemStockDO();

        itemStockDO.setItemId(itemModel.getId());
        itemStockDO.setStock(itemModel.getStock());
        return itemStockDO;
    }

    @Override
    public List<ItemModel> listItem() {
        // DAO层调用数据库查询语句
        // SELECT * FROM item ORDER BY sales DESC;
        List<ItemDO> itemDOList = itemDOMapper.listItem();
        // Java8 的 stream API map、collect、Collection.toList()
        List<ItemModel> itemModelList = itemDOList.stream().map(
                itemDO -> {
                    // DAO层调用数据库查询语句
                    // SELECT stock FROM item_stock WHERE id = itemDO.getId();
                    ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
                    // 整合两个DO为Model
                    ItemModel itemModel = this.convertModelFromDataObject(itemDO, itemStockDO);
                    return itemModel;
                }
                // 将Model转为List
        ).collect(Collectors.toList());
        return itemModelList;
    }

    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
        if (itemDO == null) {
            return null;
        }

        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());

        ItemModel itemModel = convertModelFromDataObject(itemDO, itemStockDO);
        return itemModel;
    }

    @Override
    public boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException{
        int affectedRow = itemStockDOMapper.decreaseStock(itemId, amount);
        // 更新库存成功
        if (affectedRow > 0) {
            return true;
        } else {
            return false;
        }
    }

    private ItemModel convertModelFromDataObject(ItemDO itemDO, ItemStockDO itemStockDO){
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO, itemModel);
        itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
        itemModel.setStock(itemStockDO.getStock());
        return  itemModel;
    }
}
