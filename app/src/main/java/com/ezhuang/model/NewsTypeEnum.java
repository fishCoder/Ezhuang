package com.ezhuang.model;

public enum NewsTypeEnum {

	/**
	 * 新建项目通知项目经理
	 * 
	 */
	NewPrijectNoticeToManager(1, "您有一个新的项目,被指派为项目经理"),

	/**
	 * 新建项目通知采购员
	 * 
	 */
	NewPrijectNoticeToBuyer(2, "您有一个新的项目,被指派为采购员"),

	/**
	 * 新建项目通知审核员
	 * 
	 */
	NewPrijectNoticeToChecker(3, "您有一个新的项目,被指派为审核员"),

	/**
	 * 新建项目通知质检员
	 * 
	 */
	NewPrijectNoticeToQuality(4, "您有一个新的项目,被指派为质检员"),

	/**
	 * 新建项目通知业主
	 * 
	 */
	NewPrijectNoticeToOwner(5, "您有一个新的项目"),

	/**
	 * 审核新的订单
	 * 
	 */
	NewPrijectOrderNotice(6, "您有一个新的订单需要审核"),

	/**
	 * 审核后通知项目经理
	 * 
	 */
	ProjectOrderCheckResultNoticeToManager(7, "您的订单已经审核完成"),

	/**
	 * 审核后通知采购员
	 * 
	 */
	ProjectOrderCheckResultNoticeToBuyer(8, "您有一个新的采购订单"),

	/**
	 * 新建采购订单通知
	 * 
	 */
	NewPurchaseOrderNotice(9, "您的项目订单已经采购了"),

	/**
	 * 上传家装进度通知质检员
	 * 
	 */
	NewPrijectProgressNoticeToQuality(10, "您有一个新的家装进度需要审核"),

	/**
	 * 上传家装进度通知业主
	 * 
	 */
	NewPrijectProgressNoticeToOwner(11, "您有一个新的家装进度需要审核"),

	/**
	 * 业主审核家装进度
	 * 
	 */
	OwnerCheckPrijectProgressNotice(12, "业主已经完成了家装进度的审核"),

	/**
	 * 质检员审核家装进度
	 * 
	 */
	QualityCheckPrijectProgressNotice(13, "质检员已经完成了家装进度的审核");

	public int newsType;
	public String newsContent;

	private NewsTypeEnum(Integer newsType, String newsContent) {
		this.newsType = newsType;
		this.newsContent = newsContent;
	}

}
