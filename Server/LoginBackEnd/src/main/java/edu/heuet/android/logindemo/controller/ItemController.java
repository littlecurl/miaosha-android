package edu.heuet.android.logindemo.controller;

import edu.heuet.android.logindemo.controller.viewobject.ItemVO;
import edu.heuet.android.logindemo.error.BusinessException;
import edu.heuet.android.logindemo.response.CommonReturnType;
import edu.heuet.android.logindemo.service.ItemService;
import edu.heuet.android.logindemo.service.model.ItemModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller("item")
@RequestMapping("/item")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class ItemController extends BaseController {

    private static Logger Log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    ItemService itemService;
    /*
     老师在这里写的是具体的类型
     我还是主张先用String接收
     然后再转为对应的类型
     */
    @RequestMapping(value = "create", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createItem(
            @RequestParam(name = "title")String title,
            @RequestParam(name = "description")String description,
            @RequestParam(name = "price")String price,
            @RequestParam(name = "stock")String stock,
            @RequestParam(name = "imgUrl")String imgUrl) throws BusinessException {

        // 封装参数成Model，调用Service，创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setPrice(BigDecimal.valueOf(Double.parseDouble(price)));
        itemModel.setStock(Integer.parseInt(stock));
        itemModel.setImgUrl(imgUrl);

        ItemModel itemModelForReturn = itemService.createItem(itemModel);
        ItemVO itemVO = convertVOFromModel(itemModelForReturn);

        return CommonReturnType.create(itemVO);
    }

    private ItemVO convertVOFromModel(ItemModel itemModel){
        if (itemModel == null){
            return  null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel, itemVO);
        return itemVO;
    }

    @RequestMapping(value = "get", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getItemList(@RequestParam(name="id") String idStr){
        Integer id = Integer.parseInt(idStr);

        ItemModel itemModel = itemService.getItemById(id);
        ItemVO itemVO = convertVOFromModel(itemModel);
        return CommonReturnType.create(itemVO);
    }

    @RequestMapping(value = "list", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType listItem(){
        List<ItemModel> itemModelList = itemService.listItem();
        // 使用Java8 stream API将list内的itemModel转为itemVO
        List<ItemVO> itemVOList = itemModelList.stream().map(
                itemModel -> {
                    ItemVO itemVO = this.convertVOFromModel(itemModel);
                    return itemVO;
                }
                // 将ModelList转为VOList
        ).collect(Collectors.toList());
        // Log.info("此处返回itemVOList，加上@ResponseBody，会被序列化为JSONArray");
        return CommonReturnType.create(itemVOList);
    }

}
