package com.value.decision.common.constant;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * 参数的配置，入网商户对接通联通的商户必须找相应的分公司技术颁发唯一的系统对接参数
 * 如，商户号，用户名，密码，私钥证书
 **/
//@Component
public class TLPayConfig {

	public static String merchantidjfb = "200604000002185";
	public static String usernamejfb = "20060400000218504";
	public static String pathpfxjfb = "config/20060400000218504.p12";
	public static String merchantid = "200604000011635";//200604000006146系统对接的商户号,找通联的客户经理分配
	public static String url = "https://tlt-test.allinpay.com/aipg/ProcessServlet";//对接的测试接口地址
	public static String testTranURLjfb="https://172.16.1.10:8443/merapi/ProcessServlet";
	public static String urlFileGet="https://172.16.1.11:8443/aipg/GetConFile.do?SETTDAY=@xxx&REQTIME=@yyy&MERID=@zzz&SIGN=@sss&CONTFEE=1";//简单对账文件的接口地址
	public static String username = "20060400001163504"; //200604000006146用户名 ,找通联的客户经理分配
	public static String userpass = "111111"; //用户密码,找通联的客户经理分配
	/*	public static String pathpfx = "config/20060400000729304ec.p12";/国密证书,商户公钥证书上传到通联通平台名字命名 商户号+04ec.cer*/
	public static String pathpfx = "E:\\tlPay\\gm.p12";
	public static String pfxpass = "Vm123456"; //私钥密码,找通联的客户经理分配
	public static String pathcer = "E:\\tlPay\\checkSign.cer"; //通联公钥
	public static boolean needProxy= false;
	public static final String  httpProxyIp = "";
	public static final int     httpProxyPort = 0;

	public static String quickPayNotifyUrl = "https:113.108.19.20/notify"; //快捷支付，异步通知地址

	//协议支付(310011)业务代码
	public static String quickPayBusinessCode="12301";


	@Autowired
	public TLPayConfig(@Value("${tl_pay.url}") String url,
					   @Value("${tl_pay.merchant_id}")	String merchantid,
					   @Value("${tl_pay.user_name}")	String username,
					   @Value("${tl_pay.user_pass}")	String userpass,
					   @Value("${tl_pay.path_private}")	String pathpfx,
					   @Value("${tl_pay.pfx_pass}")	String pfxpass,
					   @Value("${tl_pay.path_public}")	String pathcer,
					   @Value("${tl_pay.quick_pay_notify_url}")	String quickPayNotifyUrl,
					   @Value("${tl_pay.quick_pay_business_code}")	String quickPayBusinessCode
					) {
		TLPayConfig.url = url;
		TLPayConfig.merchantid = merchantid;
		TLPayConfig.username = username;
		TLPayConfig.userpass = userpass;
		TLPayConfig.pathpfx = pathpfx;
		TLPayConfig.pfxpass = pfxpass;
		TLPayConfig.pathcer = pathcer;
		TLPayConfig.quickPayNotifyUrl = quickPayNotifyUrl;
		TLPayConfig.quickPayBusinessCode = quickPayBusinessCode;

	}
}
