package com.risksmart.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * 路由配置信息
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouterVo {
    /** 路由名字 */
    private String name;

    /** 路由地址 */
    private String path;

    /** 是否隐藏路由 */
    private boolean hidden;

    /** 重定向地址 */
    private String redirect;

    /** 组件地址 */
    private String component;

    /** 路由参数 */
    private String query;

    /** 当children声明的路由大于1个时，自动变成嵌套模式 */
    private Boolean alwaysShow;

    /** 其他元素 */
    private MetaVo meta;

    /** 子路由 */
    private List<RouterVo> children;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public boolean getHidden() { return hidden; }
    public void setHidden(boolean hidden) { this.hidden = hidden; }
    public String getRedirect() { return redirect; }
    public void setRedirect(String redirect) { this.redirect = redirect; }
    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public Boolean getAlwaysShow() { return alwaysShow; }
    public void setAlwaysShow(Boolean alwaysShow) { this.alwaysShow = alwaysShow; }
    public MetaVo getMeta() { return meta; }
    public void setMeta(MetaVo meta) { this.meta = meta; }
    public List<RouterVo> getChildren() { return children; }
    public void setChildren(List<RouterVo> children) { this.children = children; }
}
