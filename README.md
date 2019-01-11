# apt-bindview-demo
创建Android Module命名为app
创建Java library Module命名为 apt-annotation
创建Java library Module命名为 apt-processor 依赖 apt-annotation
创建Android library Module 命名为apt-library依赖 apt-annotation、auto-service

apt-annotation：自定义注解，存放@BindView
apt-processor：注解处理器，根据apt-annotation中的注解，在编译期生成xxxActivity_ViewBinding.java代码
apt-library：工具类，调用xxxActivity_ViewBinding.java中的方法，实现View的绑定。

