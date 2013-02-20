package cn.freeteam.cms.action.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Comment;
import cn.freeteam.cms.model.Info;
import cn.freeteam.cms.service.CommentService;
import cn.freeteam.cms.service.InfoService;
import cn.freeteam.util.HtmlCode;

/**
 * 
 * <p>Title: CommentAction.java</p>
 * 
 * <p>Description: 评论相关操作</p>
 * 
 * <p>Date: Feb 20, 2013</p>
 * 
 * <p>Time: 7:36:22 PM</p>
 * 
 * <p>Copyright: 2013</p>
 * 
 * <p>Company: freeteam</p>
 * 
 * @author freeteam
 * @version 1.0
 * 
 * <p>============================================</p>
 * <p>Modification History
 * <p>Mender: </p>
 * <p>Date: </p>
 * <p>Reason: </p>
 * <p>============================================</p>
 */
public class CommentAction extends BaseAction{

	private CommentService commentService;
	private Comment comment;
	private List<Comment> commentList;
	private Map<String, String> objtypes;

	private String ValidateCode;
	private InfoService infoService;

	public CommentAction() {
		init("commentService");
	}
	/**
	 * 提交评论
	 * @return
	 */
	public String add(){

		if (ValidateCode!=null && ValidateCode.equals(getHttpSession().getAttribute("rand"))) {
			if (comment!=null && comment.getContent()!=null && comment.getContent().trim().length()>0) {
				//判断评论对象是否存在
				boolean isSubmit=false;
				if ("info".equals(comment.getObjtype())) {
					//信息
					init("infoService");
					Info info=infoService.findById(comment.getObjid());
					if (info!=null) {
						comment.setSiteid(info.getSite());
						if (Info.ISCOMMENT_NO.equals(info.getIscomment())) {
							showMessage="此信息不支持评论";
						}else if (Info.ISCOMMENT_MEMBER.equals(info.getIscomment())) {
							if (getLoginMember()!=null) {
								isSubmit=true;
							}else {
								showMessage="会员登录后才能对此信息评论";
							}
						}else if (Info.ISCOMMENT_ALL.equals(info.getIscomment())) {
							if (getLoginMember()==null) {
								//如果没有会员登录默认设置为匿名
								comment.setIsanonymous("1");
							}
							isSubmit=true;
						}
					}else {
						showMessage="此信息不存在";
					}
				}
				//提交评论
				if (isSubmit) {
					comment.setContent(HtmlCode.replaceHtml(comment.getContent()));
					if (getLoginMember()!=null) {
						comment.setMemberid(getLoginMember().getId());
						comment.setMembername(getLoginMember().getLoginname());
					}
					comment.setAddtime(new Date());
					comment.setIp(getHttpRequest().getRemoteAddr());
					if (getLoginMembergroup()!=null && getLoginMembergroup().getCommentneedcheck()==1) {
						comment.setState("1");
					}else {
						comment.setState("0");
					}
					try {
						commentService.add(comment);
						showMessage="恭喜您,提交评论成功!";
					} catch (Exception e) {
						DBProException(e);
						showMessage="提交评论失败:"+e.toString();
					}
				}
			}else {
				showMessage="请输入评论内容";
			}
		}else {
			showMessage="验证码错误";
		}
		return showMessage(showMessage, getHttpRequest().getHeader("Referer"), 3);
	}
	
	
	
	
	public CommentService getCommentService() {
		return commentService;
	}
	public void setCommentService(CommentService commentService) {
		this.commentService = commentService;
	}
	public Comment getComment() {
		return comment;
	}
	public void setComment(Comment comment) {
		this.comment = comment;
	}
	public List<Comment> getCommentList() {
		return commentList;
	}
	public void setCommentList(List<Comment> commentList) {
		this.commentList = commentList;
	}
	public Map<String, String> getObjtypes() {
		return objtypes;
	}
	public void setObjtypes(Map<String, String> objtypes) {
		this.objtypes = objtypes;
	}
	public InfoService getInfoService() {
		return infoService;
	}
	public void setInfoService(InfoService infoService) {
		this.infoService = infoService;
	}
	public String getValidateCode() {
		return ValidateCode;
	}
	public void setValidateCode(String validateCode) {
		ValidateCode = validateCode;
	}
}