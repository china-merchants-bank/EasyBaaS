(self.webpackChunkeasy_baas_frontend=self.webpackChunkeasy_baas_frontend||[]).push([[770],{25414:function(){},34952:function(Ye,Te,u){"use strict";var T=u(22122),U=u(15105),de=u(67294),Pe=function(E,j){var y={};for(var V in E)Object.prototype.hasOwnProperty.call(E,V)&&j.indexOf(V)<0&&(y[V]=E[V]);if(E!=null&&typeof Object.getOwnPropertySymbols=="function")for(var G=0,V=Object.getOwnPropertySymbols(E);G<V.length;G++)j.indexOf(V[G])<0&&Object.prototype.propertyIsEnumerable.call(E,V[G])&&(y[V[G]]=E[V[G]]);return y},Le={border:0,background:"transparent",padding:0,lineHeight:"inherit",display:"inline-block"},Ae=de.forwardRef(function(E,j){var y=function(fe){var me=fe.keyCode;me===U.Z.ENTER&&fe.preventDefault()},V=function(fe){var me=fe.keyCode,Ne=E.onClick;me===U.Z.ENTER&&Ne&&Ne()},G=E.style,t=E.noStyle,Be=E.disabled,Ze=Pe(E,["style","noStyle","disabled"]),le={};return t||(le=(0,T.Z)({},Le)),Be&&(le.pointerEvents="none"),le=(0,T.Z)((0,T.Z)({},le),G),de.createElement("div",(0,T.Z)({role:"button",tabIndex:0,ref:j},Ze,{onKeyDown:y,onKeyUp:V,style:le}))});Te.Z=Ae},95562:function(Ye,Te,u){"use strict";u.d(Te,{Z:function(){return ma}});var T=u(96156),U=u(22122),de=u(54549),Pe=u(44545),Le=u(49101),Ae=u(94184),E=u.n(Ae),j=u(28991),y=u(28481),V=u(90484),G=u(81253),t=u(67294),Be=u(31131),Ze=u(21770),le=u(5461),ve=(0,t.createContext)(null),fe=t.forwardRef(function(e,a){var o=e.prefixCls,n=e.className,r=e.style,i=e.id,l=e.active,s=e.tabKey,c=e.children;return t.createElement("div",{id:i&&"".concat(i,"-panel-").concat(s),role:"tabpanel",tabIndex:l?0:-1,"aria-labelledby":i&&"".concat(i,"-tab-").concat(s),"aria-hidden":!l,style:r,className:E()(o,l&&"".concat(o,"-active"),n),ref:a},c)}),me=fe,Ne=["key","forceRender","style","className"];function It(e){var a=e.id,o=e.activeKey,n=e.animated,r=e.tabPosition,i=e.destroyInactiveTabPane,l=t.useContext(ve),s=l.prefixCls,c=l.tabs,S=n.tabPane,P="".concat(s,"-tabpane");return t.createElement("div",{className:E()("".concat(s,"-content-holder"))},t.createElement("div",{className:E()("".concat(s,"-content"),"".concat(s,"-content-").concat(r),(0,T.Z)({},"".concat(s,"-content-animated"),S))},c.map(function(d){var x=d.key,I=d.forceRender,k=d.style,M=d.className,D=(0,G.Z)(d,Ne),L=x===o;return t.createElement(le.ZP,(0,U.Z)({key:x,visible:L,forceRender:I,removeOnLeave:!!i,leavedClassName:"".concat(P,"-hidden")},n.tabPaneMotion),function(B,Z){var $=B.style,w=B.className;return t.createElement(me,(0,U.Z)({},D,{prefixCls:P,id:a,tabKey:x,animated:S,active:L,style:(0,j.Z)((0,j.Z)({},k),$),className:E()(M,w),ref:Z}))})})))}var Qe=u(85061),Je=u(48717),kt=u(66680),qe=u(75164),Mt=u(42550),_e={width:0,height:0,left:0,top:0};function Ot(e,a,o){return(0,t.useMemo)(function(){for(var n,r=new Map,i=a.get((n=e[0])===null||n===void 0?void 0:n.key)||_e,l=i.left+i.width,s=0;s<e.length;s+=1){var c=e[s].key,S=a.get(c);if(!S){var P;S=a.get((P=e[s-1])===null||P===void 0?void 0:P.key)||_e}var d=r.get(c)||(0,j.Z)({},S);d.right=l-d.left-d.width,r.set(c,d)}return r},[e.map(function(n){return n.key}).join("_"),a,o])}function et(e,a){var o=t.useRef(e),n=t.useState({}),r=(0,y.Z)(n,2),i=r[1];function l(s){var c=typeof s=="function"?s(o.current):s;c!==o.current&&a(c,o.current),o.current=c,i({})}return[o.current,l]}var Lt=.1,tt=.01,Re=20,at=Math.pow(.995,Re);function At(e,a){var o=(0,t.useState)(),n=(0,y.Z)(o,2),r=n[0],i=n[1],l=(0,t.useState)(0),s=(0,y.Z)(l,2),c=s[0],S=s[1],P=(0,t.useState)(0),d=(0,y.Z)(P,2),x=d[0],I=d[1],k=(0,t.useState)(),M=(0,y.Z)(k,2),D=M[0],L=M[1],B=(0,t.useRef)();function Z(f){var h=f.touches[0],v=h.screenX,R=h.screenY;i({x:v,y:R}),window.clearInterval(B.current)}function $(f){if(!!r){f.preventDefault();var h=f.touches[0],v=h.screenX,R=h.screenY;i({x:v,y:R});var p=v-r.x,C=R-r.y;a(p,C);var Y=Date.now();S(Y),I(Y-c),L({x:p,y:C})}}function w(){if(!!r&&(i(null),L(null),D)){var f=D.x/x,h=D.y/x,v=Math.abs(f),R=Math.abs(h);if(Math.max(v,R)<Lt)return;var p=f,C=h;B.current=window.setInterval(function(){if(Math.abs(p)<tt&&Math.abs(C)<tt){window.clearInterval(B.current);return}p*=at,C*=at,a(p*Re,C*Re)},Re)}}var O=(0,t.useRef)();function N(f){var h=f.deltaX,v=f.deltaY,R=0,p=Math.abs(h),C=Math.abs(v);p===C?R=O.current==="x"?h:v:p>C?(R=h,O.current="x"):(R=v,O.current="y"),a(-R,-R)&&f.preventDefault()}var K=(0,t.useRef)(null);K.current={onTouchStart:Z,onTouchMove:$,onTouchEnd:w,onWheel:N},t.useEffect(function(){function f(p){K.current.onTouchStart(p)}function h(p){K.current.onTouchMove(p)}function v(p){K.current.onTouchEnd(p)}function R(p){K.current.onWheel(p)}return document.addEventListener("touchmove",h,{passive:!1}),document.addEventListener("touchend",v,{passive:!1}),e.current.addEventListener("touchstart",f,{passive:!1}),e.current.addEventListener("wheel",R),function(){document.removeEventListener("touchmove",h),document.removeEventListener("touchend",v)}},[])}var Bt=u(8410);function nt(e){var a=(0,t.useState)(0),o=(0,y.Z)(a,2),n=o[0],r=o[1],i=(0,t.useRef)(0),l=(0,t.useRef)();return l.current=e,(0,Bt.o)(function(){var s;(s=l.current)===null||s===void 0||s.call(l)},[n]),function(){i.current===n&&(i.current+=1,r(i.current))}}function Dt(e){var a=(0,t.useRef)([]),o=(0,t.useState)({}),n=(0,y.Z)(o,2),r=n[1],i=(0,t.useRef)(typeof e=="function"?e():e),l=nt(function(){var c=i.current;a.current.forEach(function(S){c=S(c)}),a.current=[],i.current=c,r({})});function s(c){a.current.push(c),l()}return[i.current,s]}var rt={width:0,height:0,left:0,top:0,right:0};function wt(e,a,o,n,r,i,l){var s=l.tabs,c=l.tabPosition,S=l.rtl,P,d,x;return["top","bottom"].includes(c)?(P="width",d=S?"right":"left",x=Math.abs(o)):(P="height",d="top",x=-o),(0,t.useMemo)(function(){if(!s.length)return[0,0];for(var I=s.length,k=I,M=0;M<I;M+=1){var D=e.get(s[M].key)||rt;if(D[d]+D[P]>x+a){k=M-1;break}}for(var L=0,B=I-1;B>=0;B-=1){var Z=e.get(s[B].key)||rt;if(Z[d]<x){L=B+1;break}}return[L,k]},[e,a,n,r,i,x,c,s.map(function(I){return I.key}).join("_"),S])}function ot(e){var a;return e instanceof Map?(a={},e.forEach(function(o,n){a[n]=o})):a=e,JSON.stringify(a)}var Kt="TABS_DQ";function it(e){return String(e).replace(/"/g,Kt)}function zt(e,a){var o=e.prefixCls,n=e.editable,r=e.locale,i=e.style;return!n||n.showAdd===!1?null:t.createElement("button",{ref:a,type:"button",className:"".concat(o,"-nav-add"),style:i,"aria-label":(r==null?void 0:r.addAriaLabel)||"Add tab",onClick:function(s){n.onEdit("add",{event:s})}},n.addIcon||"+")}var lt=t.forwardRef(zt),Wt=t.forwardRef(function(e,a){var o=e.position,n=e.prefixCls,r=e.extra;if(!r)return null;var i,l={};return(0,V.Z)(r)==="object"&&!t.isValidElement(r)?l=r:l.right=r,o==="right"&&(i=l.right),o==="left"&&(i=l.left),i?t.createElement("div",{className:"".concat(n,"-extra-content"),ref:a},i):null}),st=Wt,Ut=u(96753),ct=u(94423),Q=u(15105);function jt(e,a){var o=e.prefixCls,n=e.id,r=e.tabs,i=e.locale,l=e.mobile,s=e.moreIcon,c=s===void 0?"More":s,S=e.moreTransitionName,P=e.style,d=e.className,x=e.editable,I=e.tabBarGutter,k=e.rtl,M=e.removeAriaLabel,D=e.onTabClick,L=e.getPopupContainer,B=e.popupClassName,Z=(0,t.useState)(!1),$=(0,y.Z)(Z,2),w=$[0],O=$[1],N=(0,t.useState)(null),K=(0,y.Z)(N,2),f=K[0],h=K[1],v="".concat(n,"-more-popup"),R="".concat(o,"-dropdown"),p=f!==null?"".concat(v,"-").concat(f):null,C=i==null?void 0:i.dropdownAriaLabel;function Y(m,z){m.preventDefault(),m.stopPropagation(),x.onEdit("remove",{key:z,event:m})}var ye=t.createElement(ct.ZP,{onClick:function(z){var J=z.key,H=z.domEvent;D(J,H),O(!1)},prefixCls:"".concat(R,"-menu"),id:v,tabIndex:-1,role:"listbox","aria-activedescendant":p,selectedKeys:[f],"aria-label":C!==void 0?C:"expanded dropdown"},r.map(function(m){var z=x&&m.closable!==!1&&!m.disabled;return t.createElement(ct.sN,{key:m.key,id:"".concat(v,"-").concat(m.key),role:"option","aria-controls":n&&"".concat(n,"-panel-").concat(m.key),disabled:m.disabled},t.createElement("span",null,m.label),z&&t.createElement("button",{type:"button","aria-label":M||"remove",tabIndex:0,className:"".concat(R,"-menu-item-remove"),onClick:function(H){H.stopPropagation(),Y(H,m.key)}},m.closeIcon||x.removeIcon||"\xD7"))}));function te(m){for(var z=r.filter(function(ce){return!ce.disabled}),J=z.findIndex(function(ce){return ce.key===f})||0,H=z.length,ae=0;ae<H;ae+=1){J=(J+m+H)%H;var pe=z[J];if(!pe.disabled){h(pe.key);return}}}function X(m){var z=m.which;if(!w){[Q.Z.DOWN,Q.Z.SPACE,Q.Z.ENTER].includes(z)&&(O(!0),m.preventDefault());return}switch(z){case Q.Z.UP:te(-1),m.preventDefault();break;case Q.Z.DOWN:te(1),m.preventDefault();break;case Q.Z.ESC:O(!1);break;case Q.Z.SPACE:case Q.Z.ENTER:f!==null&&D(f,m);break}}(0,t.useEffect)(function(){var m=document.getElementById(p);m&&m.scrollIntoView&&m.scrollIntoView(!1)},[f]),(0,t.useEffect)(function(){w||h(null)},[w]);var ee=(0,T.Z)({},k?"marginRight":"marginLeft",I);r.length||(ee.visibility="hidden",ee.order=1);var he=E()((0,T.Z)({},"".concat(R,"-rtl"),k)),se=l?null:t.createElement(Ut.Z,{prefixCls:R,overlay:ye,trigger:["hover"],visible:r.length?w:!1,transitionName:S,onVisibleChange:O,overlayClassName:E()(he,B),mouseEnterDelay:.1,mouseLeaveDelay:.1,getPopupContainer:L},t.createElement("button",{type:"button",className:"".concat(o,"-nav-more"),style:ee,tabIndex:-1,"aria-hidden":"true","aria-haspopup":"listbox","aria-controls":v,id:"".concat(n,"-more"),"aria-expanded":w,onKeyDown:X},c));return t.createElement("div",{className:E()("".concat(o,"-nav-operations"),d),style:P,ref:a},se,t.createElement(lt,{prefixCls:o,locale:i,editable:x}))}var Vt=t.memo(t.forwardRef(jt),function(e,a){return a.tabMoving});function $t(e){var a,o=e.prefixCls,n=e.id,r=e.active,i=e.tab,l=i.key,s=i.label,c=i.disabled,S=i.closeIcon,P=e.closable,d=e.renderWrapper,x=e.removeAriaLabel,I=e.editable,k=e.onClick,M=e.onFocus,D=e.style,L="".concat(o,"-tab"),B=I&&P!==!1&&!c;function Z(O){c||k(O)}function $(O){O.preventDefault(),O.stopPropagation(),I.onEdit("remove",{key:l,event:O})}var w=t.createElement("div",{key:l,"data-node-key":it(l),className:E()(L,(a={},(0,T.Z)(a,"".concat(L,"-with-remove"),B),(0,T.Z)(a,"".concat(L,"-active"),r),(0,T.Z)(a,"".concat(L,"-disabled"),c),a)),style:D,onClick:Z},t.createElement("div",{role:"tab","aria-selected":r,id:n&&"".concat(n,"-tab-").concat(l),className:"".concat(L,"-btn"),"aria-controls":n&&"".concat(n,"-panel-").concat(l),"aria-disabled":c,tabIndex:c?null:0,onClick:function(N){N.stopPropagation(),Z(N)},onKeyDown:function(N){[Q.Z.SPACE,Q.Z.ENTER].includes(N.which)&&(N.preventDefault(),Z(N))},onFocus:M},s),B&&t.createElement("button",{type:"button","aria-label":x||"remove",tabIndex:0,className:"".concat(L,"-remove"),onClick:function(N){N.stopPropagation(),$(N)}},S||I.removeIcon||"\xD7"));return d?d(w):w}var Ft=$t,be=function(a){var o=a.current||{},n=o.offsetWidth,r=n===void 0?0:n,i=o.offsetHeight,l=i===void 0?0:i;return[r,l]},Ie=function(a,o){return a[o?0:1]};function Ht(e,a){var o,n=t.useContext(ve),r=n.prefixCls,i=n.tabs,l=e.className,s=e.style,c=e.id,S=e.animated,P=e.activeKey,d=e.rtl,x=e.extra,I=e.editable,k=e.locale,M=e.tabPosition,D=e.tabBarGutter,L=e.children,B=e.onTabClick,Z=e.onTabScroll,$=(0,t.useRef)(),w=(0,t.useRef)(),O=(0,t.useRef)(),N=(0,t.useRef)(),K=(0,t.useRef)(),f=(0,t.useRef)(),h=(0,t.useRef)(),v=M==="top"||M==="bottom",R=et(0,function(g,b){v&&Z&&Z({direction:g>b?"left":"right"})}),p=(0,y.Z)(R,2),C=p[0],Y=p[1],ye=et(0,function(g,b){!v&&Z&&Z({direction:g>b?"top":"bottom"})}),te=(0,y.Z)(ye,2),X=te[0],ee=te[1],he=(0,t.useState)([0,0]),se=(0,y.Z)(he,2),m=se[0],z=se[1],J=(0,t.useState)([0,0]),H=(0,y.Z)(J,2),ae=H[0],pe=H[1],ce=(0,t.useState)([0,0]),ge=(0,y.Z)(ce,2),De=ge[0],we=ge[1],Ke=(0,t.useState)([0,0]),Ee=(0,y.Z)(Ke,2),ze=Ee[0],We=Ee[1],A=Dt(new Map),ne=(0,y.Z)(A,2),Se=ne[0],ba=ne[1],ke=Ot(i,Se,ae[0]),Ue=Ie(m,v),Ce=Ie(ae,v),je=Ie(De,v),ft=Ie(ze,v),mt=Ue<Ce+je,q=mt?Ue-ft:Ue-je,pa="".concat(r,"-nav-operations-hidden"),re=0,ue=0;v&&d?(re=0,ue=Math.max(0,Ce-q)):(re=Math.min(0,q-Ce),ue=0);function Ve(g){return g<re?re:g>ue?ue:g}var bt=(0,t.useRef)(),ya=(0,t.useState)(),pt=(0,y.Z)(ya,2),Me=pt[0],yt=pt[1];function $e(){yt(Date.now())}function Fe(){window.clearTimeout(bt.current)}At(N,function(g,b){function W(F,ie){F(function(_){var Za=Ve(_+ie);return Za})}return mt?(v?W(Y,g):W(ee,b),Fe(),$e(),!0):!1}),(0,t.useEffect)(function(){return Fe(),Me&&(bt.current=window.setTimeout(function(){yt(0)},100)),Fe},[Me]);var ha=wt(ke,q,v?C:X,Ce,je,ft,(0,j.Z)((0,j.Z)({},e),{},{tabs:i})),ht=(0,y.Z)(ha,2),ga=ht[0],Ea=ht[1],gt=(0,kt.Z)(function(){var g=arguments.length>0&&arguments[0]!==void 0?arguments[0]:P,b=ke.get(g)||{width:0,height:0,left:0,right:0,top:0};if(v){var W=C;d?b.right<C?W=b.right:b.right+b.width>C+q&&(W=b.right+b.width-q):b.left<-C?W=-b.left:b.left+b.width>-C+q&&(W=-(b.left+b.width-q)),ee(0),Y(Ve(W))}else{var F=X;b.top<-X?F=-b.top:b.top+b.height>-X+q&&(F=-(b.top+b.height-q)),Y(0),ee(Ve(F))}}),Oe={};M==="top"||M==="bottom"?Oe[d?"marginRight":"marginLeft"]=D:Oe.marginTop=D;var Et=i.map(function(g,b){var W=g.key;return t.createElement(Ft,{id:c,prefixCls:r,key:W,tab:g,style:b===0?void 0:Oe,closable:g.closable,editable:I,active:W===P,renderWrapper:L,removeAriaLabel:k==null?void 0:k.removeAriaLabel,onClick:function(ie){B(W,ie)},onFocus:function(){gt(W),$e(),!!N.current&&(d||(N.current.scrollLeft=0),N.current.scrollTop=0)}})}),St=function(){return ba(function(){var b=new Map;return i.forEach(function(W){var F,ie=W.key,_=(F=K.current)===null||F===void 0?void 0:F.querySelector('[data-node-key="'.concat(it(ie),'"]'));_&&b.set(ie,{width:_.offsetWidth,height:_.offsetHeight,left:_.offsetLeft,top:_.offsetTop})}),b})};(0,t.useEffect)(function(){St()},[i.map(function(g){return g.key}).join("_")]);var He=nt(function(){var g=be($),b=be(w),W=be(O);z([g[0]-b[0]-W[0],g[1]-b[1]-W[1]]);var F=be(h);we(F);var ie=be(f);We(ie);var _=be(K);pe([_[0]-F[0],_[1]-F[1]]),St()}),Sa=i.slice(0,ga),Ca=i.slice(Ea+1),Ct=[].concat((0,Qe.Z)(Sa),(0,Qe.Z)(Ca)),xa=(0,t.useState)(),xt=(0,y.Z)(xa,2),Ta=xt[0],Pa=xt[1],oe=ke.get(P),Tt=(0,t.useRef)();function Pt(){qe.Z.cancel(Tt.current)}(0,t.useEffect)(function(){var g={};return oe&&(v?(d?g.right=oe.right:g.left=oe.left,g.width=oe.width):(g.top=oe.top,g.height=oe.height)),Pt(),Tt.current=(0,qe.Z)(function(){Pa(g)}),Pt},[oe,v,d]),(0,t.useEffect)(function(){gt()},[P,re,ue,ot(oe),ot(ke),v]),(0,t.useEffect)(function(){He()},[d]);var Zt=!!Ct.length,xe="".concat(r,"-nav-wrap"),Ge,Xe,Nt,Rt;return v?d?(Xe=C>0,Ge=C!==ue):(Ge=C<0,Xe=C!==re):(Nt=X<0,Rt=X!==re),t.createElement(Je.Z,{onResize:He},t.createElement("div",{ref:(0,Mt.x1)(a,$),role:"tablist",className:E()("".concat(r,"-nav"),l),style:s,onKeyDown:function(){$e()}},t.createElement(st,{ref:w,position:"left",extra:x,prefixCls:r}),t.createElement("div",{className:E()(xe,(o={},(0,T.Z)(o,"".concat(xe,"-ping-left"),Ge),(0,T.Z)(o,"".concat(xe,"-ping-right"),Xe),(0,T.Z)(o,"".concat(xe,"-ping-top"),Nt),(0,T.Z)(o,"".concat(xe,"-ping-bottom"),Rt),o)),ref:N},t.createElement(Je.Z,{onResize:He},t.createElement("div",{ref:K,className:"".concat(r,"-nav-list"),style:{transform:"translate(".concat(C,"px, ").concat(X,"px)"),transition:Me?"none":void 0}},Et,t.createElement(lt,{ref:h,prefixCls:r,locale:k,editable:I,style:(0,j.Z)((0,j.Z)({},Et.length===0?void 0:Oe),{},{visibility:Zt?"hidden":null})}),t.createElement("div",{className:E()("".concat(r,"-ink-bar"),(0,T.Z)({},"".concat(r,"-ink-bar-animated"),S.inkBar)),style:Ta})))),t.createElement(Vt,(0,U.Z)({},e,{removeAriaLabel:k==null?void 0:k.removeAriaLabel,ref:f,prefixCls:r,tabs:Ct,className:!Zt&&pa,tabMoving:!!Me})),t.createElement(st,{ref:O,position:"right",extra:x,prefixCls:r})))}var ut=t.forwardRef(Ht),Gt=["renderTabBar"],Xt=["label","key"];function Yt(e){var a=e.renderTabBar,o=(0,G.Z)(e,Gt),n=t.useContext(ve),r=n.tabs;if(a){var i=(0,j.Z)((0,j.Z)({},o),{},{panes:r.map(function(l){var s=l.label,c=l.key,S=(0,G.Z)(l,Xt);return t.createElement(me,(0,U.Z)({tab:s,key:c,tabKey:c},S))})});return a(i,ut)}return t.createElement(ut,o)}var Na=u(80334);function Qt(){var e=arguments.length>0&&arguments[0]!==void 0?arguments[0]:{inkBar:!0,tabPane:!1},a;return e===!1?a={inkBar:!1,tabPane:!1}:e===!0?a={inkBar:!0,tabPane:!1}:a=(0,j.Z)({inkBar:!0},(0,V.Z)(e)==="object"?e:{}),a.tabPaneMotion&&a.tabPane===void 0&&(a.tabPane=!0),!a.tabPaneMotion&&a.tabPane&&(a.tabPane=!1),a}var Jt=["id","prefixCls","className","items","direction","activeKey","defaultActiveKey","editable","animated","tabPosition","tabBarGutter","tabBarStyle","tabBarExtraContent","locale","moreIcon","moreTransitionName","destroyInactiveTabPane","renderTabBar","onChange","onTabClick","onTabScroll","getPopupContainer","popupClassName"],dt=0;function qt(e,a){var o,n=e.id,r=e.prefixCls,i=r===void 0?"rc-tabs":r,l=e.className,s=e.items,c=e.direction,S=e.activeKey,P=e.defaultActiveKey,d=e.editable,x=e.animated,I=e.tabPosition,k=I===void 0?"top":I,M=e.tabBarGutter,D=e.tabBarStyle,L=e.tabBarExtraContent,B=e.locale,Z=e.moreIcon,$=e.moreTransitionName,w=e.destroyInactiveTabPane,O=e.renderTabBar,N=e.onChange,K=e.onTabClick,f=e.onTabScroll,h=e.getPopupContainer,v=e.popupClassName,R=(0,G.Z)(e,Jt),p=t.useMemo(function(){return(s||[]).filter(function(A){return A&&(0,V.Z)(A)==="object"&&"key"in A})},[s]),C=c==="rtl",Y=Qt(x),ye=(0,t.useState)(!1),te=(0,y.Z)(ye,2),X=te[0],ee=te[1];(0,t.useEffect)(function(){ee((0,Be.Z)())},[]);var he=(0,Ze.Z)(function(){var A;return(A=p[0])===null||A===void 0?void 0:A.key},{value:S,defaultValue:P}),se=(0,y.Z)(he,2),m=se[0],z=se[1],J=(0,t.useState)(function(){return p.findIndex(function(A){return A.key===m})}),H=(0,y.Z)(J,2),ae=H[0],pe=H[1];(0,t.useEffect)(function(){var A=p.findIndex(function(Se){return Se.key===m});if(A===-1){var ne;A=Math.max(0,Math.min(ae,p.length-1)),z((ne=p[A])===null||ne===void 0?void 0:ne.key)}pe(A)},[p.map(function(A){return A.key}).join("_"),m,ae]);var ce=(0,Ze.Z)(null,{value:n}),ge=(0,y.Z)(ce,2),De=ge[0],we=ge[1];(0,t.useEffect)(function(){n||(we("rc-tabs-".concat(dt)),dt+=1)},[]);function Ke(A,ne){K==null||K(A,ne);var Se=A!==m;z(A),Se&&(N==null||N(A))}var Ee={id:De,activeKey:m,animated:Y,tabPosition:k,rtl:C,mobile:X},ze,We=(0,j.Z)((0,j.Z)({},Ee),{},{editable:d,locale:B,moreIcon:Z,moreTransitionName:$,tabBarGutter:M,onTabClick:Ke,onTabScroll:f,extra:L,style:D,panes:null,getPopupContainer:h,popupClassName:v});return t.createElement(ve.Provider,{value:{tabs:p,prefixCls:i}},t.createElement("div",(0,U.Z)({ref:a,id:n,className:E()(i,"".concat(i,"-").concat(k),(o={},(0,T.Z)(o,"".concat(i,"-mobile"),X),(0,T.Z)(o,"".concat(i,"-editable"),d),(0,T.Z)(o,"".concat(i,"-rtl"),C),o),l)},R),ze,t.createElement(Yt,(0,U.Z)({},We,{renderTabBar:O})),t.createElement(It,(0,U.Z)({destroyInactiveTabPane:w},Ee,{animated:Y}))))}var _t=t.forwardRef(qt),ea=_t,ta=ea,aa=u(53124),na=u(97647),ra=u(33603),oa={motionAppear:!1,motionEnter:!0,motionLeave:!0};function ia(e){var a=arguments.length>1&&arguments[1]!==void 0?arguments[1]:{inkBar:!0,tabPane:!1},o;return a===!1?o={inkBar:!1,tabPane:!1}:a===!0?o={inkBar:!0,tabPane:!0}:o=(0,U.Z)({inkBar:!0},(0,V.Z)(a)==="object"?a:{}),o.tabPane&&(o.tabPaneMotion=(0,U.Z)((0,U.Z)({},oa),{motionName:(0,ra.mL)(e,"switch")})),o}var la=u(50344),sa=function(e,a){var o={};for(var n in e)Object.prototype.hasOwnProperty.call(e,n)&&a.indexOf(n)<0&&(o[n]=e[n]);if(e!=null&&typeof Object.getOwnPropertySymbols=="function")for(var r=0,n=Object.getOwnPropertySymbols(e);r<n.length;r++)a.indexOf(n[r])<0&&Object.prototype.propertyIsEnumerable.call(e,n[r])&&(o[n[r]]=e[n[r]]);return o};function ca(e){return e.filter(function(a){return a})}function ua(e,a){if(e)return e;var o=(0,la.Z)(a).map(function(n){if(t.isValidElement(n)){var r=n.key,i=n.props,l=i||{},s=l.tab,c=sa(l,["tab"]),S=(0,U.Z)((0,U.Z)({key:String(r)},c),{label:s});return S}return null});return ca(o)}var da=function(){return null},va=da,fa=function(e,a){var o={};for(var n in e)Object.prototype.hasOwnProperty.call(e,n)&&a.indexOf(n)<0&&(o[n]=e[n]);if(e!=null&&typeof Object.getOwnPropertySymbols=="function")for(var r=0,n=Object.getOwnPropertySymbols(e);r<n.length;r++)a.indexOf(n[r])<0&&Object.prototype.propertyIsEnumerable.call(e,n[r])&&(o[n[r]]=e[n[r]]);return o};function vt(e){var a=e.type,o=e.className,n=e.size,r=e.onEdit,i=e.hideAdd,l=e.centered,s=e.addIcon,c=e.children,S=e.items,P=e.animated,d=fa(e,["type","className","size","onEdit","hideAdd","centered","addIcon","children","items","animated"]),x=d.prefixCls,I=d.moreIcon,k=I===void 0?t.createElement(Pe.Z,null):I,M=t.useContext(aa.E_),D=M.getPrefixCls,L=M.direction,B=M.getPopupContainer,Z=D("tabs",x),$;a==="editable-card"&&($={onEdit:function(f,h){var v=h.key,R=h.event;r==null||r(f==="add"?R:v,f)},removeIcon:t.createElement(de.Z,null),addIcon:s||t.createElement(Le.Z,null),showAdd:i!==!0});var w=D(),O=ua(S,c),N=ia(Z,P);return t.createElement(na.Z.Consumer,null,function(K){var f,h=n!==void 0?n:K;return t.createElement(ta,(0,U.Z)({direction:L,getPopupContainer:B,moreTransitionName:"".concat(w,"-slide-up")},d,{items:O,className:E()((f={},(0,T.Z)(f,"".concat(Z,"-").concat(h),h),(0,T.Z)(f,"".concat(Z,"-card"),["card","editable-card"].includes(a)),(0,T.Z)(f,"".concat(Z,"-editable-card"),a==="editable-card"),(0,T.Z)(f,"".concat(Z,"-centered"),l),f),o),editable:$,moreIcon:k,prefixCls:Z,animated:N}))})}vt.TabPane=va;var ma=vt},18106:function(Ye,Te,u){"use strict";var T=u(38663),U=u.n(T),de=u(25414),Pe=u.n(de)}}]);
