package com.ezhuang.model;

import com.ezhuang.R;

public enum NewsTypeEnum {

	/**
	 * 新建项目通知项目经理
	 * 
	 */
	NewPrijectNoticeToManager(1, "您有一个新的项目,被指派为项目经理", R.mipmap.mes_new_project_manager),

	/**
	 * 新建项目通知采购员
	 * 
	 */
	NewPrijectNoticeToBuyer(2, "您有一个新的项目,被指派为采购员",R.mipmap.mes_new_project_buy),

	/**
	 * 新建项目通知审核员
	 * 
	 */
	NewPrijectNoticeToChecker(3, "您有一个新的项目,被指派为审核员",R.mipmap.mes_new_project_check),

	/**
	 * 新建项目通知质检员
	 * 
	 */
	NewPrijectNoticeToQuality(4, "您有一个新的项目,被指派为质检员",R.mipmap.mes_quality),

	/**
	 * 新建项目通知业主
	 * 
	 */
	NewPrijectNoticeToOwner(5, "您有一个新的项目",R.mipmap.ic_default_image),

	/**
	 * 审核新的订单
	 * 
	 */
	NewPrijectOrderNotice(6, "您有一个新的订单需要审核",R.mipmap.mes_checked),

	/**
	 * 审核后通知项目经理
	 * 
	 */
    ProjectOrderCheckPassNoticeToManager(7, "您的订单已经通过审核",R.mipmap.mes_check_pass),

	/**
	 * 审核后通知采购员
	 * 
	 */
	ProjectOrderCheckResultNoticeToBuyer(8, "您有一个新的采购订单",R.mipmap.mes_will_buy),

	/**
	 * 新建采购订单通知
	 * 
	 */
	NewPurchaseOrderNotice(9, "您的项目订单已经采购了",R.mipmap.mes_all_buy),

	/**
	 * 上传家装进度通知质检员
	 * 
	 */
	NewPrijectProgressNoticeToQuality(10, "您有一个新的家装进度需要审核",R.mipmap.mes_will_progress),

	/**
	 * 上传家装进度通知业主
	 * 
	 */
	NewPrijectProgressNoticeToOwner(11, "您有一个新的家装进度需要审核",R.mipmap.ic_default_image),

	/**
	 * 业主审核家装进度
	 * 
	 */
	OwnerCheckPrijectProgressNotice(12, "业主已经完成了家装进度的审核",R.mipmap.mes_progressed),

	/**
	 * 质检员审核家装进度
	 * 
	 */
	QualityCheckPrijectProgressNotice(13, "质检员已经完成了家装进度的审核",R.mipmap.mes_progressed),

    /**
     *
     */
    ProjectOrderCheckNotPassNoticeToManager(14, "您的订单已经未通过审核",R.mipmap.mes_check_unpass),

	BmbOrderDispatch(15,"你有一个订单需要调度",R.mipmap.ic_default_image),

    BmbOrderStorage(16,"你有一个订单需要出库",R.mipmap.ic_default_image),

    ProjectOrderHasSendOutToManager(17,"订单已出库",R.mipmap.ic_default_image),

    ProjectOrderHasSendOutToBuyer(18,"订单已出库",R.mipmap.ic_default_image),

	ConfirmReceiptGoodsToDingdy(19,"您有一个订单项已经确认签收",R.mipmap.ic_default_image),

	ConfirmReceiptGoodsToBuyer(20,"您采购的订单项已到场确认签收",R.mipmap.ic_default_image);

    public int newsType;
	public String newsContent;
    public int newIcon;

	private NewsTypeEnum(Integer newsType, String newsContent,int newIcon) {
		this.newsType = newsType;
		this.newsContent = newsContent;
        this.newIcon = newIcon;
	}

}
