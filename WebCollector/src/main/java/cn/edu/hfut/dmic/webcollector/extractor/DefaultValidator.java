package cn.edu.hfut.dmic.webcollector.extractor;

import org.apache.commons.lang.StringUtils;

/**
 * Created by jerry on 2015/1/21.
 */
public class DefaultValidator implements Validator {
    @Override
    public String validateHtml(String content) {
        if(content == null)
            return null;
        String divStart = "<div";
        String divEnd = "</div>";
        String validatedStr = content.trim();
        //去掉开头结尾的div
        if(validatedStr.startsWith(divStart) && validatedStr.endsWith(divEnd)){
            validatedStr = validatedStr.replaceFirst("(?is)<div.*?>","");
            validatedStr = validatedStr.substring(0,validatedStr.lastIndexOf(divEnd));
        }
        int divStartNum = StringUtils.countMatches(validatedStr, divStart);
        int divEndNum = StringUtils.countMatches(validatedStr,divEnd);
        //中间的div如果不匹配的话，统一改成<p>
        if(divStartNum != divEndNum){
            validatedStr = validatedStr.replaceAll("(?is)<div.*?>","<p>").replaceAll(divEnd,"</p>");
        }
        return validatedStr.replaceAll("(?is)<!--.*?-->", " ").trim();
    }

    public static void main(String []args){
        DefaultValidator v = new DefaultValidator();
        String content = "<div class=\"t_news_txt\">\n" +
                "<p>　　在茫茫无际的大海上，有一座美丽的海岛。<div>金色的沙滩上长着<div>许多高高的椰子树，</div>树顶上的椰子又大又多，椰子树的身体都向大海倾斜。</p><p>　　海鸥问一棵身体倾斜得特别厉害的椰子树：“你身上的椰子长得特别多，沉重的负担让你倾斜着身体生活，难道不感到特别累吗?”</p><p>　　椰子树说：“说实话，这样倾斜着身体，确实很累。可椰子是我的孩子，只要一想到让他们快点儿长大，再苦再累我都心甘情愿啊!”</p><p>　　突然，椰子树上的一颗椰子“扑通”一声掉进了波涛汹涌的大海中。海鸥惊叫一声，对椰子树说：“不好了，你的孩子掉到大海中去了，他会被淹死的!”</p><p>　　椰子树微微一笑，平静地说：“我是故意把他放入大海的，让他随着海流自己去寻找落脚生根的地方。他已经长大成熟了。”</p><p>　　海鸥说：“让他就在你身边生根发芽，长成大树不是更好，为什么一定要让他漂泊到异乡?”</p><p>　　椰子树说：“孩子小的时候，我精心呵护他们，让他们健康成长;孩子成熟了，我就要鼓励他们独自出去寻找适合生长的地方。孩子老是依恋在我身旁没有出息，世界那么大，应该四海为家!”</p><p>　　海鸥说：“大海环境险恶让孩子自己去闯世界你是否觉得有点残酷?”</p><p>　　椰子树说：“把孩子放进大海，让他在风浪中磨炼，让他自己去谋生，我觉得这不是对孩子残酷，而是对他最深沉的爱!”</p></div>\n";
        System.out.println(v.validateHtml(content));
    }
}
