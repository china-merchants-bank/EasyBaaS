(self.webpackChunkeasy_baas_frontend=self.webpackChunkeasy_baas_frontend||[]).push([[803],{52953:function(){},60304:function(ee,z,e){"use strict";e.r(z),e.d(z,{default:function(){return Re}});var Z=e(20228),N=e(11382),P=e(11849),le=e(62350),G=e(24565),w=e(90636),J=e(34792),$=e(48086),M=e(3182),se=e(57663),O=e(71577),W=e(57337),Q=e(38663),ie=e(52953),R=e(96156),te=e(28481),Y=e(90484),ne=e(94184),L=e.n(ne),m=e(50344),t=e(67294),A=e(53124),s=e(96159),X=e(24308),ve=function(n){var a=n.children;return a},he=ve,x=e(22122);function q(u){return u!=null}var ye=function(n){var a=n.itemPrefixCls,i=n.component,f=n.span,p=n.className,c=n.style,b=n.labelStyle,F=n.contentStyle,S=n.bordered,h=n.label,D=n.content,T=n.colon,j=i;if(S){var E;return t.createElement(j,{className:L()((E={},(0,R.Z)(E,"".concat(a,"-item-label"),q(h)),(0,R.Z)(E,"".concat(a,"-item-content"),q(D)),E),p),style:c,colSpan:f},q(h)&&t.createElement("span",{style:b},h),q(D)&&t.createElement("span",{style:F},D))}return t.createElement(j,{className:L()("".concat(a,"-item"),p),style:c,colSpan:f},t.createElement("div",{className:"".concat(a,"-item-container")},(h||h===0)&&t.createElement("span",{className:L()("".concat(a,"-item-label"),(0,R.Z)({},"".concat(a,"-item-no-colon"),!T)),style:b},h),(D||D===0)&&t.createElement("span",{className:L()("".concat(a,"-item-content")),style:F},D)))},re=ye;function ae(u,n,a){var i=n.colon,f=n.prefixCls,p=n.bordered,c=a.component,b=a.type,F=a.showLabel,S=a.showContent,h=a.labelStyle,D=a.contentStyle;return u.map(function(T,j){var E=T.props,V=E.label,g=E.children,y=E.prefixCls,C=y===void 0?f:y,v=E.className,l=E.style,o=E.labelStyle,B=E.contentStyle,U=E.span,d=U===void 0?1:U,H=T.key;return typeof c=="string"?t.createElement(re,{key:"".concat(b,"-").concat(H||j),className:v,style:l,labelStyle:(0,x.Z)((0,x.Z)({},h),o),contentStyle:(0,x.Z)((0,x.Z)({},D),B),span:d,colon:i,component:c,itemPrefixCls:C,bordered:p,label:F?V:null,content:S?g:null}):[t.createElement(re,{key:"label-".concat(H||j),className:v,style:(0,x.Z)((0,x.Z)((0,x.Z)({},h),l),o),span:1,colon:i,component:c[0],itemPrefixCls:C,bordered:p,label:V}),t.createElement(re,{key:"content-".concat(H||j),className:v,style:(0,x.Z)((0,x.Z)((0,x.Z)({},D),l),B),span:d*2-1,component:c[1],itemPrefixCls:C,bordered:p,content:g})]})}var Ce=function(n){var a=t.useContext(oe),i=n.prefixCls,f=n.vertical,p=n.row,c=n.index,b=n.bordered;return f?t.createElement(t.Fragment,null,t.createElement("tr",{key:"label-".concat(c),className:"".concat(i,"-row")},ae(p,n,(0,x.Z)({component:"th",type:"label",showLabel:!0},a))),t.createElement("tr",{key:"content-".concat(c),className:"".concat(i,"-row")},ae(p,n,(0,x.Z)({component:"td",type:"content",showContent:!0},a)))):t.createElement("tr",{key:c,className:"".concat(i,"-row")},ae(p,n,(0,x.Z)({component:b?["th","td"]:"td",type:"item",showLabel:!0,showContent:!0},a)))},Ee=Ce,oe=t.createContext({}),ce={xxl:3,xl:3,lg:3,md:3,sm:2,xs:1};function ge(u,n){if(typeof u=="number")return u;if((0,Y.Z)(u)==="object")for(var a=0;a<X.c4.length;a++){var i=X.c4[a];if(n[i]&&u[i]!==void 0)return u[i]||ce[i]}return 3}function de(u,n,a){var i=u;return(n===void 0||n>a)&&(i=(0,s.Tm)(u,{span:a})),i}function Ze(u,n){var a=(0,m.Z)(u).filter(function(c){return c}),i=[],f=[],p=n;return a.forEach(function(c,b){var F,S=(F=c.props)===null||F===void 0?void 0:F.span,h=S||1;if(b===a.length-1){f.push(de(c,S,p)),i.push(f);return}h<p?(p-=h,f.push(c)):(f.push(de(c,h,p)),i.push(f),p=n,f=[])}),i}function me(u){var n,a=u.prefixCls,i=u.title,f=u.extra,p=u.column,c=p===void 0?ce:p,b=u.colon,F=b===void 0?!0:b,S=u.bordered,h=u.layout,D=u.children,T=u.className,j=u.style,E=u.size,V=u.labelStyle,g=u.contentStyle,y=t.useContext(A.E_),C=y.getPrefixCls,v=y.direction,l=C("descriptions",a),o=t.useState({}),B=(0,te.Z)(o,2),U=B[0],d=B[1],H=ge(c,U);t.useEffect(function(){var ue=X.ZP.subscribe(function(_){(0,Y.Z)(c)==="object"&&d(_)});return function(){X.ZP.unsubscribe(ue)}},[]);var Ie=Ze(D,H),Te=t.useMemo(function(){return{labelStyle:V,contentStyle:g}},[V,g]);return t.createElement(oe.Provider,{value:Te},t.createElement("div",{className:L()(l,(n={},(0,R.Z)(n,"".concat(l,"-").concat(E),E&&E!=="default"),(0,R.Z)(n,"".concat(l,"-bordered"),!!S),(0,R.Z)(n,"".concat(l,"-rtl"),v==="rtl"),n),T),style:j},(i||f)&&t.createElement("div",{className:"".concat(l,"-header")},i&&t.createElement("div",{className:"".concat(l,"-title")},i),f&&t.createElement("div",{className:"".concat(l,"-extra")},f)),t.createElement("div",{className:"".concat(l,"-view")},t.createElement("table",null,t.createElement("tbody",null,Ie.map(function(ue,_){return t.createElement(Ee,{key:_,index:_,colon:F,prefixCls:l,vertical:h==="vertical",bordered:S,row:ue})}))))))}me.Item=he;var I=me,Ae=e(67265),Fe=e(69126),pe=e(37476),K=e(5966),Be=e(27398),k=e(89686),be=e(49101),Me=e(71194),Se=e(50146),Le=e(12968),xe=e(20352),Oe=e(402),fe=e(61859),De=e(81394),je=e.n(De),r=e(85893),we=function(n){var a=n.modalVisible,i=n.onFinish,f=n.onCancel,p=(0,t.useState)(!1),c=(0,W.Z)(p,2),b=c[0],F=c[1],S=fe.Z.Paragraph;return(0,r.jsxs)(r.Fragment,{children:[(0,r.jsxs)(pe.Y,{title:"\u914D\u7F6E\u544A\u8B66\u670D\u52A1\u53D1\u4EF6\u90AE\u7BB1",width:"40%",modalProps:{destroyOnClose:!0,onCancel:function(){f()}},visible:a,onFinish:i,children:[(0,r.jsx)(K.Z,{name:"alarmUsername",label:"\u90AE\u7BB1\u5730\u5740",rules:[{required:!0}]}),(0,r.jsx)(K.Z,{name:"alarmPassword",label:"\u6388\u6743\u7801",rules:[{required:!0}]}),(0,r.jsx)(K.Z,{name:"smtpAddress",label:"smtp\u5730\u5740",rules:[{required:!0}]}),(0,r.jsx)(O.Z,{type:"link",onClick:function(){F(!0)},children:"\u586B\u5199\u8BF4\u660E"})]}),(0,r.jsx)(Se.Z,{title:"\u586B\u5199\u8BF4\u660E",width:"70%",visible:b,onCancel:function(){F(!1)},footer:null,children:(0,r.jsxs)(fe.Z,{children:[(0,r.jsx)(S,{children:"1\u3001\u53C2\u8003\u4E0B\u56FE\uFF0C\u5F00\u542Fsmtp\u670D\u52A1\uFF0C\u751F\u6210\u6388\u6743\u7801\u5E76\u8BB0\u5F55"}),(0,r.jsx)(xe.Z,{src:je(),width:"80%"}),(0,r.jsx)(S,{children:"2\u3001\u5C06\u83B7\u53D6\u7684\u6388\u6743\u7801\u4E0E\u90AE\u7BB1\u5BF9\u5E94\u7684smtp\u5730\u5740\u586B\u5165\u8868\u5355\u4E2D"}),(0,r.jsx)(S,{children:(0,r.jsxs)("ul",{children:[(0,r.jsx)("li",{children:"qq\u90AE\u7BB1smtp\u5730\u5740\uFF1Asmtp.qq.com:465"}),(0,r.jsx)("li",{children:"163\u90AE\u7BB1smtp\u5730\u5740\uFF1Asmtp.163.com:465"}),(0,r.jsx)("li",{children:"126\u90AE\u7BB1smtp\u5730\u5740\uFF1Asmtp.126.com:465"})]})})]})})]})},Pe=we,Ne=(0,r.jsxs)(I,{title:"\u544A\u8B66\u89E6\u53D1\u6761\u4EF6\u8BF4\u660E\uFF1A",column:4,children:[(0,r.jsx)(I.Item,{children:"1\u3001CITA\u8282\u70B9\u6574\u4F53\u8FD0\u884C\u72B6\u6001\u5F02\u5E38\u544A\u8B66"}),(0,r.jsx)(I.Item,{children:"2\u3001CITA\u8282\u70B9\u5404\u4E2A\u670D\u52A1\u8FDB\u7A0B\u8FD0\u884C\u72B6\u6001\u5F02\u5E38\u544A\u8B66"}),(0,r.jsx)(I.Item,{children:"3\u3001CITA\u533A\u5757\u9AD8\u5EA6\u4E0D\u4E00\u81F4\u65F6\u544A\u8B66\uFF08\u591A\u4E2A\u8282\u70B9\u65F6\u751F\u6548\uFF09"}),(0,r.jsx)(I.Item,{children:"4\u3001CITA\u8282\u70B9\u533A\u5757\u9AD8\u5EA6\u8D85\u8FC720s\u672A\u589E\u957F\u544A\u8B66"}),(0,r.jsx)(I.Item,{children:"5\u3001\u76D1\u63A7\u7EC4\u4EF6\u4E0D\u53EF\u7528\u544A\u8B66"}),(0,r.jsx)(I.Item,{children:"6\u3001cpu\u4F7F\u7528\u7387\u8D85\u8FC770%\u544A\u8B66"}),(0,r.jsx)(I.Item,{children:"7\u3001\u5185\u5B58\u4F7F\u7528\u7387\u8D85\u8FC770%\u544A\u8B66"}),(0,r.jsx)(I.Item,{children:"8.\u786C\u76D8\u4F7F\u7528\u7387\u8D85\u8FC770%\u544A\u8B66"}),(0,r.jsx)(I.Item,{children:"\u76F8\u540C\u5185\u5BB9\u544A\u8B66\u6BCF8\u5C0F\u65F6\u91CD\u590D\u53D1\u9001\u4E00\u6B21\uFF0C\u544A\u8B66\u72B6\u6001\u6062\u590D\u540C\u6837\u4F1A\u53D1\u9001\u6062\u590D\u90AE\u4EF6\u63D0\u9192"})]}),$e=function(){var n=(0,t.useState)(!1),a=(0,W.Z)(n,2),i=a[0],f=a[1],p=(0,t.useState)(!1),c=(0,W.Z)(p,2),b=c[0],F=c[1],S=(0,t.useState)(!1),h=(0,W.Z)(S,2),D=h[0],T=h[1],j=(0,t.useRef)(),E=(0,r.jsx)(O.Z,{type:"primary",onClick:function(){T(!0)},children:"\u544A\u8B66\u670D\u52A1\u53D1\u4EF6\u90AE\u7BB1\u914D\u7F6E"}),V=[{title:"\u5E8F\u53F7",valueType:"index",width:"10%"},{title:"\u544A\u8B66\u4EBA\u59D3\u540D",dataIndex:"alertName",editable:!1,width:"30%"},{title:"\u544A\u8B66\u4EBA\u90AE\u7BB1",dataIndex:"alertEmail",width:"40%",fieldProps:{allowClear:!1},formItemProps:{rules:[{required:!0,message:"\u544A\u8B66\u4EBA\u90AE\u7BB1\u4E0D\u80FD\u4E3A\u7A7A"},{max:128,message:"\u90AE\u7BB1\u957F\u5EA6\u4E0D\u8D85\u8FC7128\u5B57\u7B26"},{pattern:/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/,message:"\u90AE\u7BB1\u683C\u5F0F\u9519\u8BEF"}]}},{title:"\u64CD\u4F5C",valueType:"option",width:"20%",render:function(y,C,v,l){return[(0,r.jsx)("a",{onClick:function(){var B;l==null||(B=l.startEditable)===null||B===void 0||B.call(l,C.key)},children:"\u4FEE\u6539"},"update"),(0,r.jsx)(G.Z,{title:"\u786E\u8BA4\u5220\u9664\uFF1F",onConfirm:(0,M.Z)((0,w.Z)().mark(function o(){var B;return(0,w.Z)().wrap(function(d){for(;;)switch(d.prev=d.next){case 0:return f(!0),d.prev=1,d.next=4,(0,k.yo)(C.alertEmail);case 4:$.default.success("\u5220\u9664\u6210\u529F"),(B=j.current)===null||B===void 0||B.reload(),d.next=11;break;case 8:d.prev=8,d.t0=d.catch(1),$.default.error("\u5220\u9664\u5931\u8D25");case 11:f(!1);case 12:case"end":return d.stop()}},o,null,[[1,8]])})),children:(0,r.jsx)(O.Z,{type:"link",danger:!0,children:"\u5220\u9664"})},"delete")]}}];return(0,r.jsxs)(Ae.ZP,{content:Ne,extra:E,children:[(0,r.jsx)(N.Z,{spinning:i,size:"large",children:(0,r.jsx)(Fe.ZP,{headerTitle:"\u544A\u8B66\u4EBA\u5217\u8868",columns:V,search:!1,rowKey:"key",actionRef:j,request:(0,M.Z)((0,w.Z)().mark(function g(){var y,C,v;return(0,w.Z)().wrap(function(o){for(;;)switch(o.prev=o.next){case 0:return o.next=2,(0,k.aN)();case 2:return y=o.sent,C=y.code,v=y.data,o.abrupt("return",{data:v||[],success:C===Be.hN,total:v.length||0});case 6:case"end":return o.stop()}},g)})),postData:function(y){return y.map(function(C,v){return(0,P.Z)((0,P.Z)({},C),{},{key:"alert".concat(v)})})},toolbar:{actions:[(0,r.jsxs)(O.Z,{type:"primary",onClick:function(){F(!0)},children:[(0,r.jsx)(be.Z,{})," \u65B0\u589E"]},"add")]},options:{reload:!0,density:!1,setting:!1},pagination:{hideOnSinglePage:!0,showSizeChanger:!1,pageSize:10},editable:{type:"single",actionRender:function(y,C,v){return[v.save,v.cancel]},onSave:function(){var g=(0,M.Z)((0,w.Z)().mark(function C(v,l){var o,B;return(0,w.Z)().wrap(function(d){for(;;)switch(d.prev=d.next){case 0:return d.prev=0,B={alertName:l.alertName,alertEmail:l.alertEmail},d.next=4,(0,k.Cr)(B);case 4:$.default.success("\u4FEE\u6539\u6210\u529F"),(o=j.current)===null||o===void 0||o.reload(),d.next=11;break;case 8:d.prev=8,d.t0=d.catch(0),$.default.error("\u4FEE\u6539\u5931\u8D25");case 11:case"end":return d.stop()}},C,null,[[0,8]])}));function y(C,v){return g.apply(this,arguments)}return y}()},form:{ignoreRules:!1}})}),(0,r.jsxs)(pe.Y,{title:"\u65B0\u589E\u544A\u8B66\u4EBA",width:"30%",modalProps:{destroyOnClose:!0},visible:b,onVisibleChange:F,onFinish:function(){var g=(0,M.Z)((0,w.Z)().mark(function y(C){var v;return(0,w.Z)().wrap(function(o){for(;;)switch(o.prev=o.next){case 0:return o.prev=0,o.next=3,(0,k.V_)(C);case 3:F(!1),$.default.success("\u65B0\u589E\u6210\u529F"),(v=j.current)===null||v===void 0||v.reload(),o.next=11;break;case 8:o.prev=8,o.t0=o.catch(0),$.default.error("\u65B0\u589E\u5931\u8D25");case 11:case"end":return o.stop()}},y,null,[[0,8]])}));return function(y){return g.apply(this,arguments)}}(),children:[(0,r.jsx)(K.Z,{name:"alertName",label:"\u59D3\u540D",rules:[{required:!0,message:"\u8BF7\u8F93\u5165\u59D3\u540D"},{max:64,message:"\u59D3\u540D\u957F\u5EA6\u4E0D\u8D85\u8FC764\u5B57\u7B26"}]}),(0,r.jsx)(K.Z,{name:"alertEmail",label:"\u90AE\u7BB1",rules:[{required:!0,message:"\u8BF7\u8F93\u5165\u90AE\u7BB1"},{max:128,message:"\u90AE\u7BB1\u957F\u5EA6\u4E0D\u8D85\u8FC7128\u5B57\u7B26"},{pattern:/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/,message:"\u90AE\u7BB1\u683C\u5F0F\u9519\u8BEF"}]})]}),(0,r.jsx)(Pe,{modalVisible:D,onFinish:function(){var g=(0,M.Z)((0,w.Z)().mark(function y(C){return(0,w.Z)().wrap(function(l){for(;;)switch(l.prev=l.next){case 0:return l.prev=0,l.next=3,(0,k._7)(C);case 3:return $.default.success("\u914D\u7F6E\u544A\u8B66\u670D\u52A1\u53D1\u4EF6\u90AE\u7BB1\u6210\u529F"),T(!1),l.abrupt("return",Promise.resolve(!0));case 8:return l.prev=8,l.t0=l.catch(0),$.default.error("\u914D\u7F6E\u544A\u8B66\u670D\u52A1\u53D1\u4EF6\u90AE\u7BB1\u5931\u8D25"),l.abrupt("return",Promise.resolve(!1));case 12:case"end":return l.stop()}},y,null,[[0,8]])}));return function(y){return g.apply(this,arguments)}}(),onCancel:function(){T(!1)}})]})},Re=$e},89686:function(ee,z,e){"use strict";e.d(z,{CI:function(){return le},p7:function(){return w},yt:function(){return $},aN:function(){return se},V_:function(){return W},Cr:function(){return ie},yo:function(){return te},_7:function(){return ne}});var Z=e(90636),N=e(3182),P=e(64805);function le(){return G.apply(this,arguments)}function G(){return G=(0,N.Z)((0,Z.Z)().mark(function m(){return(0,Z.Z)().wrap(function(A){for(;;)switch(A.prev=A.next){case 0:return A.abrupt("return",(0,P.WY)("/monitor/component/list"));case 1:case"end":return A.stop()}},m)})),G.apply(this,arguments)}function w(m){return J.apply(this,arguments)}function J(){return J=(0,N.Z)((0,Z.Z)().mark(function m(t){return(0,Z.Z)().wrap(function(s){for(;;)switch(s.prev=s.next){case 0:return s.abrupt("return",(0,P.WY)("/monitor/component/startOrStop",{method:"PUT",data:t}));case 1:case"end":return s.stop()}},m)})),J.apply(this,arguments)}function $(m){return M.apply(this,arguments)}function M(){return M=(0,N.Z)((0,Z.Z)().mark(function m(t){return(0,Z.Z)().wrap(function(s){for(;;)switch(s.prev=s.next){case 0:return s.abrupt("return",(0,P.WY)("/monitor/component/updateGrafanaConfig",{method:"POST",data:t}));case 1:case"end":return s.stop()}},m)})),M.apply(this,arguments)}function se(){return O.apply(this,arguments)}function O(){return O=(0,N.Z)((0,Z.Z)().mark(function m(){return(0,Z.Z)().wrap(function(A){for(;;)switch(A.prev=A.next){case 0:return A.abrupt("return",(0,P.WY)("/monitor/alertRule/queryAllAlertEmails"));case 1:case"end":return A.stop()}},m)})),O.apply(this,arguments)}function W(m){return Q.apply(this,arguments)}function Q(){return Q=(0,N.Z)((0,Z.Z)().mark(function m(t){return(0,Z.Z)().wrap(function(s){for(;;)switch(s.prev=s.next){case 0:return s.abrupt("return",(0,P.WY)("/monitor/alertRule/addAlertEmails",{method:"POST",data:t}));case 1:case"end":return s.stop()}},m)})),Q.apply(this,arguments)}function ie(m){return R.apply(this,arguments)}function R(){return R=(0,N.Z)((0,Z.Z)().mark(function m(t){return(0,Z.Z)().wrap(function(s){for(;;)switch(s.prev=s.next){case 0:return s.abrupt("return",(0,P.WY)("/monitor/alertRule/updateAlertEmails",{method:"PUT",data:t}));case 1:case"end":return s.stop()}},m)})),R.apply(this,arguments)}function te(m){return Y.apply(this,arguments)}function Y(){return Y=(0,N.Z)((0,Z.Z)().mark(function m(t){return(0,Z.Z)().wrap(function(s){for(;;)switch(s.prev=s.next){case 0:return s.abrupt("return",(0,P.WY)("/monitor/alertRule/deleteAlertEmails",{method:"DELETE",params:{alertEmail:t}}));case 1:case"end":return s.stop()}},m)})),Y.apply(this,arguments)}function ne(m){return L.apply(this,arguments)}function L(){return L=(0,N.Z)((0,Z.Z)().mark(function m(t){return(0,Z.Z)().wrap(function(s){for(;;)switch(s.prev=s.next){case 0:return s.abrupt("return",(0,P.WY)("/monitor/alertRule/addAlertSendEmails",{method:"POST",data:t}));case 1:case"end":return s.stop()}},m)})),L.apply(this,arguments)}},81394:function(ee,z,e){ee.exports=e.p+"static/smtpConfig.3a073be6.jpg"}}]);