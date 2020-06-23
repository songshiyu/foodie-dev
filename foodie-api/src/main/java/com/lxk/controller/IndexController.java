package com.lxk.controller;

import com.lxk.enums.YesOrNo;
import com.lxk.pojo.Carousel;
import com.lxk.pojo.Category;
import com.lxk.pojo.vo.CategoryVO;
import com.lxk.pojo.vo.NewItemsVO;
import com.lxk.service.CarouselService;
import com.lxk.service.CategoryService;
import com.lxk.utils.ResultJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * @author songshiyu
 * @date 2020/6/14 17:59
 **/

@Api(value = "首页", tags = "首页展示的相关接口")
@RestController
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private CategoryService categoryService;

    @ApiOperation(value = "获取首页轮播图列表", notes = "获取首页轮播图列表", httpMethod = "GET")
    @GetMapping("/carousel")
    public ResultJSONResult carousel() {
        List<Carousel> carousels = carouselService.queryAll(YesOrNo.YES.type);
        return ResultJSONResult.ok(carousels);
    }

    /**
     * 首页分类展示需求：
     *  1.第一次刷新主页查询大分类，渲染展示到首页
     *  2.如果鼠标上移到大分类，则加载其子分类的内容，如果已经存在子分类，则不需要加载(懒加载)
     * */
    @ApiOperation(value = "用户获取商品分类(一级分类)", notes = "用户获取商品分类(一级分类)", httpMethod = "GET")
    @GetMapping("/cats")
    public ResultJSONResult cats() {
        List<Category> categories = categoryService.queryAllRootLevelcat();
        return ResultJSONResult.ok(categories);
    }

    @ApiOperation(value = "获取商品子分类", notes = "获取商品子分类", httpMethod = "GET")
    @GetMapping("/subCat/{rootCatId}")
    public ResultJSONResult subCat(
            @ApiParam(name = "rootCatId",value = "一级分类id",required = true)
            @PathVariable Integer rootCatId) {

        if (rootCatId == null){
            return ResultJSONResult.errorMsg("分类不存在");
        }

        List<CategoryVO> subCatLIst = categoryService.getSubCatLIst(rootCatId);
        return ResultJSONResult.ok(subCatLIst);
    }

    @ApiOperation(value = "查询每个一级分类下6个最新的商品信息", notes = "查询每个一级分类下6个最新的商品信息", httpMethod = "GET")
    @GetMapping("/sixNewItems/{rootCatId}")
    public ResultJSONResult sixNewItems(
            @ApiParam(name = "rootCatId",value = "一级分类id",required = true)
            @PathVariable Integer rootCatId) {

        if (rootCatId == null){
            return ResultJSONResult.errorMsg("分类不存在");
        }

        List<NewItemsVO> sixNewItemslazyList = categoryService.getSixNewItemslazy(rootCatId);
        return ResultJSONResult.ok(sixNewItemslazyList);
    }
}
