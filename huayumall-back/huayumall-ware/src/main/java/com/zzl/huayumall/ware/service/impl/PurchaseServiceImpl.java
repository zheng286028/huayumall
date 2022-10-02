package com.zzl.huayumall.ware.service.impl;

import com.zzl.common.constant.wareConstant;
import com.zzl.huayumall.ware.entity.PurchaseDetailEntity;
import com.zzl.huayumall.ware.exception.purchaseMergeException;
import com.zzl.huayumall.ware.service.PurchaseDetailService;
import com.zzl.huayumall.ware.service.WareSkuService;
import com.zzl.huayumall.ware.vo.mergeVo;
import com.zzl.huayumall.ware.vo.purchaseDetailDoneVo;
import com.zzl.huayumall.ware.vo.purchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.Query;

import com.zzl.huayumall.ware.dao.PurchaseDao;
import com.zzl.huayumall.ware.entity.PurchaseEntity;
import com.zzl.huayumall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {
    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private PurchaseDetailService purchaseDetailService;
    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseEntity> wrapper = new QueryWrapper<>();
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 查询采购单状态为新建/已领取的
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageAndunReceive(Map<String, Object> params) {
        QueryWrapper<PurchaseEntity> wrapper = new QueryWrapper<>();
        //status=0:新建，status=1:已领取
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),wrapper.eq("status",0).or().eq("status",1)
        );
        return new PageUtils(page);
    }

    /**
     * 合并采购需求
     * @param vo
     */
    @Override
    @Transactional
    public void MergePurchaseArrivePurchaseOrder(mergeVo vo) {
        //1、如果没有指定purchaseId，那么就是创建采购单，并合并到该单上
        Long purchaseId = vo.getPurchaseId();
        if(purchaseId==null || purchaseId==0){
            //没指定，创建采购单
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(wareConstant.purchaseEnum.STATUS_NEW.getCode());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setCreateTime(new Date());
            //创建
            purchaseService.save(purchaseEntity);
            //新的purchaseId
            purchaseId = purchaseEntity.getId();
        }
        //指定了，修改采购需求即可，批量修改
        List<Long> items = vo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect = items.stream().map(item -> {
            PurchaseDetailEntity entity = purchaseDetailService.getOne(new QueryWrapper<PurchaseDetailEntity>().eq("id",item).
                    eq("status",wareConstant.purchaseDetailEnum.STATUS_NEW.getCode()).or().eq("status",wareConstant.purchaseDetailEnum.STATUS_ASSIGNED.getCode()));
                if(entity != null){
                    PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                    purchaseDetailEntity.setId(item);
                    purchaseDetailEntity.setPurchaseId(finalPurchaseId);
                    purchaseDetailEntity.setStatus(wareConstant.purchaseDetailEnum.STATUS_ASSIGNED.getCode());
                    return purchaseDetailEntity;
                }
                return null;
        }).filter(wa->{
            // todo 这里需要优化
            if(wa != null){
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        //批量修改
        if(!collect.isEmpty() && collect.size()>0){
            purchaseDetailService.updateBatchById(collect);
        }else{
            //todo 无法自定义处理该异常
            throw new purchaseMergeException("该采购需求正被采购中！");
        }
    }

    /**
     * 修改菜单单状态和采购需求
     * @param purchaseIds
     */
    @Transactional
    @Override
    public void receivedPurchaseByPurchaseIds(List<Long> purchaseIds) {
        //查询采购单，条件为新建的或已分配的
        List<PurchaseEntity> collect = purchaseIds.stream().map(id -> {
            PurchaseEntity byId = this.getById(id);
            return byId;
        }).filter(status -> {
            //新建的或者已分配的
            if(status.getStatus() == wareConstant.purchaseEnum.STATUS_NEW.getCode() || status.getStatus() == wareConstant.purchaseEnum.STATUS_ASSIGNED.getCode()){
                return true;
            }
            return false;
        }).map(setUp -> {
            setUp.setStatus(wareConstant.purchaseEnum.STATUS_RECEIVED.getCode());
            setUp.setUpdateTime(new Date());
            return setUp;
        }).collect(Collectors.toList());
        //批量修改
        if(!collect.isEmpty() && collect.size()>0){
            this.updateBatchById(collect);
        }
        //修改采购需求状态
        purchaseIds.forEach(id->{
            List<PurchaseDetailEntity> list = purchaseDetailService.updateStatusByPurchaseId(id);
            //批量修改状态
            if(!list.isEmpty() && list.size()>0){
                List<PurchaseDetailEntity> PurchaseDetailEntityList = list.stream().map(item -> {
                    PurchaseDetailEntity entity = new PurchaseDetailEntity();
                    entity.setId(item.getId());
                    entity.setStatus(wareConstant.purchaseDetailEnum.STATUS_PURCHASING.getCode());
                    return entity;
                }).collect(Collectors.toList());
                //修改
                purchaseDetailService.updateBatchById(PurchaseDetailEntityList);
            }
        });
    }

    /**
     * 完成采购
     * @param vo
     */
    @Transactional
    @Override
    public void purchaseDone(purchaseDoneVo vo) {
        //1、修改采购需求状态
        List<purchaseDetailDoneVo> items = vo.getItems();
        Boolean flag = true;
        PurchaseDetailEntity purchaseDetail = null;
        List<PurchaseDetailEntity> purchaseDetailEntities = new ArrayList<>();
        for (purchaseDetailDoneVo item : items) {
          if(item.getStatus() == wareConstant.purchaseDetailEnum.STATUS_PURCHASING_FAIL.getCode()){
              purchaseDetail = new PurchaseDetailEntity();
              flag = false;
              //修改状态
              purchaseDetail.setStatus(item.getStatus());
          }else{
              //采购完成
              purchaseDetail = new PurchaseDetailEntity();
              purchaseDetail.setStatus(item.getStatus());
              //3、入库
              //根据采购需求查询skuID
              PurchaseDetailEntity detailEntity = purchaseDetailService.getById(item.getItemId());
              //完成入库
              wareSkuService.addStock(detailEntity.getSkuId(),detailEntity.getSkuNum(),detailEntity.getWareId());

          }
          //获取参数
            purchaseDetail.setId(item.getItemId());
            purchaseDetailEntities.add(purchaseDetail);
        }
        //批量修改采购需求
        purchaseDetailService.updateBatchById(purchaseDetailEntities);
        //2、采购需求全部完成才修改采购状态
        PurchaseEntity purchase = new PurchaseEntity();
        purchase.setId(vo.getId());
        purchase.setUpdateTime(new Date());
        //根据flag来决定采购单状态
        purchase.setStatus(flag?wareConstant.purchaseEnum.STATUS_COMPLETED.getCode():wareConstant.purchaseEnum.STATUS_EXCEPTION.getCode());
        //修改
        this.updateById(purchase);
    }
}
