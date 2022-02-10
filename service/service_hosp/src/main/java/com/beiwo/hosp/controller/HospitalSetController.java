package com.beiwo.hosp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.beiwo.yygh.common.result.Result;
import com.beiwo.yygh.common.utils.MD5;
import com.beiwo.yygh.model.hosp.HospitalSet;
import com.beiwo.hosp.service.HospitalSetService;
import com.beiwo.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    // 1 查询医院设置表中所有消息
    @ApiOperation(value = "获取所有医院设置信息")
    @GetMapping("/findAll")
   public Result<List<HospitalSet>> findAllHospitalSet(){
        //调用service的方法
        List<HospitalSet> list = hospitalSetService.list();
        return  Result.ok(list);
    }


    //逻辑删除医院设置
    @ApiOperation(value = "删除医院设置信息")
    @DeleteMapping("/{id}")
    public Result<HospitalSet> removeHospitalSet(@PathVariable("id") Long id){
        boolean flag = hospitalSetService.removeById(id);
        if(flag){
           return  Result.ok();
        }else {
            return Result.fail();
        }
    }

    //3  条件分页查询
    @PostMapping("findPage/{current}/{limit}")
    public Result<Page<HospitalSet>> findPageHospitalSet(
                                 @PathVariable("current") Long current,
                                   @PathVariable("limit") Long limit,
                                   @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo){
        Page<HospitalSet> page = new Page<>(current,limit);
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper();
        String hosname = hospitalSetQueryVo.getHosname();
        String hoscode = hospitalSetQueryVo.getHoscode();
        if(!StringUtils.isEmpty(hosname)){
            wrapper.eq("hosname",hospitalSetQueryVo.getHosname());
        }
        if(!StringUtils.isEmpty(hoscode)){
            wrapper.eq("hoscode",hospitalSetQueryVo.getHoscode());
        }

        Page<HospitalSet> hospitalSet = hospitalSetService.page(page, wrapper);

        //返回结果
        return  Result.ok(hospitalSet);

    }

    //4 添加医院设置
    @PostMapping("/saveHospitalSet")
    public Result<HospitalSet> saveHospitalSet(@RequestBody HospitalSet hospitalSet){
        //设置状态  1使用  0 不能使用
        hospitalSet.setStatus(1);
        //签名密钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+random.nextInt(1000)));

        //调用service
        boolean save = hospitalSetService.save(hospitalSet);
        if(save){
            return Result.ok();
        }else {
            return Result.fail();
        }

    }


    //5 根据id获取医院设置
    @GetMapping("/getHospitalSet/{id}")
    public Result<HospitalSet> getHospitalSet(@PathVariable("id") Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        if(hospitalSet!=null){
            return Result.ok(hospitalSet);
        }else {
            return Result.fail();
        }
    }

    //6 修改医院设置
    @PostMapping("/updateHospitalSet")
    public Result<HospitalSet> updateHospitalSet(@RequestBody HospitalSet hospitalSet){
        boolean flag = hospitalSetService.updateById(hospitalSet);
        if(flag){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    //7 批量删除医院设置
    @DeleteMapping("/bathRemoveHospitalSet")
    public Result<HospitalSet> bathRemoveHospitalSet(@RequestBody List<String> idList){
        hospitalSetService.removeBatchByIds(idList);
        return Result.ok();
    }

    //8 医院设置锁定和解锁
    @PutMapping("lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable("id") Long id,
                                  @PathVariable("status") Integer status){
        //根据id查询医院设置信息
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        //设置状态
        hospitalSet.setStatus(status);
        //调用方法
        hospitalSetService.updateById(hospitalSet);
        return Result.ok();
    }


   //9 发送签名密钥
   @PutMapping("sendKey/{id}")
   public Result lockHospitalSet(@PathVariable("id") Long id) {
       HospitalSet hospitalSet = hospitalSetService.getById(id);
       String signKey = hospitalSet.getSignKey();
       String hoscode = hospitalSet.getHoscode();
       //todo
       return Result.ok();
   }


}
