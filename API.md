## /课表
```text
暂无描述
```
#### 公共Header参数
参数名 | 示例值 | 参数描述
--- | --- | ---
暂无参数
#### 公共Query参数
参数名 | 示例值 | 参数描述
--- | --- | ---
暂无参数
#### 公共Body参数
参数名 | 示例值 | 参数描述
--- | --- | ---
暂无参数
#### 预执行脚本
```javascript
暂无预执行脚本
```
#### 后执行脚本
```javascript
暂无后执行脚本
```
## /课表/更新课表
```text
暂无描述
```
#### 接口状态
> 开发中

#### 接口URL
> localhost:8080/api/classtable

#### 请求方式
> PUT

#### Content-Type
> form-data

#### 请求Body参数
参数名 | 示例值 | 参数类型 | 是否必填 | 参数描述
--- | --- | --- | --- | ---
studentId | 17034480111 | Text | 是 | 学号
studentPassword |  | Text | 是 | 教务系统密码
schoolYear | 202101 | Text | 是 | 学年学期(形如: 202101 其中2021代表2021-2022学年 01代表上学期 02代表下学期
#### 预执行脚本
```javascript
暂无预执行脚本
```
#### 后执行脚本
```javascript
暂无后执行脚本
```
## /课表/删除课表
```text
暂无描述
```
#### 接口状态
> 开发中

#### 接口URL
> localhost:8080/api/classtable

#### 请求方式
> DELETE

#### Content-Type
> json

#### 请求Body参数
```javascript
[//定义一个学号数组
	17034480126 
]
```
参数名 | 示例值 | 参数类型 | 参数描述
--- | --- | --- | ---
0 | 17034480126 | Text | 学号数组
#### 预执行脚本
```javascript
暂无预执行脚本
```
#### 后执行脚本
```javascript
暂无后执行脚本
```
## /课表/获取课表
```text
暂无描述
```
#### 接口状态
> 开发中

#### 接口URL
> localhost:8080/api/classtable

#### 请求方式
> POST

#### Content-Type
> form-data

#### 请求Body参数
参数名 | 示例值 | 参数类型 | 是否必填 | 参数描述
--- | --- | --- | --- | ---
studentId | 17034480126 | Text | 是 | 学号
studentPassword |  | Text | 是 | 教务系统密码
schoolYear | 202101 | Text | 是 | 学年学期(形如: 202101 其中2021代表2021-2022学年 01代表上学期 02代表下学期
#### 预执行脚本
```javascript
暂无预执行脚本
```
#### 后执行脚本
```javascript
暂无后执行脚本
```
#### 成功响应示例
```javascript
{
	"code": true,
	"data": [
		{
			"studentId": 17034480126,
			"courseName": "大型数据库技术",
			"courseClass": "0506",
			"courseLocation": "硬件实验室",
			"courseWeekDay": "4",
			"courseWeek": "5",
			"courseSchoolYear": "202101",
			"courseSchoolYearTerm": "202101",
			"courseTeacher": "吴良海"
		}
	]
}
```
#### 失败响应示例
```javascript
{"msg":"账号密码错误!","code":false}
```
## /素拓分
```text
暂无描述
```
#### 公共Header参数
参数名 | 示例值 | 参数描述
--- | --- | ---
暂无参数
#### 公共Query参数
参数名 | 示例值 | 参数描述
--- | --- | ---
暂无参数
#### 公共Body参数
参数名 | 示例值 | 参数描述
--- | --- | ---
暂无参数
#### 预执行脚本
```javascript
暂无预执行脚本
```
#### 后执行脚本
```javascript
暂无后执行脚本
```
## /素拓分/获取素拓分列表
```text
暂无描述
```
#### 接口状态
> 开发中

#### 接口URL
> localhost:8080/api/qeactivity

#### 请求方式
> POST

#### Content-Type
> form-data

#### 请求Body参数
参数名 | 示例值 | 参数类型 | 是否必填 | 参数描述
--- | --- | --- | --- | ---
studentId | 17034480111 | Text | 是 | 学号
studentPassword |  | Text | 是 | 教务系统密码
#### 预执行脚本
```javascript
暂无预执行脚本
```
#### 后执行脚本
```javascript
暂无后执行脚本
```
## /素拓分/更新素拓列表
```text
暂无描述
```
#### 接口状态
> 开发中

#### 接口URL
> localhost:8080/api/qeactivity

#### 请求方式
> PUT

#### Content-Type
> form-data

#### 请求Body参数
参数名 | 示例值 | 参数类型 | 是否必填 | 参数描述
--- | --- | --- | --- | ---
studentId | 17034480111 | Text | 是 | 学号
studentPassword |  | Text | 是 | 教务系统密码
#### 预执行脚本
```javascript
暂无预执行脚本
```
#### 后执行脚本
```javascript
暂无后执行脚本
```
## /素拓分/删除素拓列表
```text
暂无描述
```
#### 接口状态
> 开发中

#### 接口URL
> localhost:8080/api/qeactivity

#### 请求方式
> DELETE

#### Content-Type
> json

#### 请求Body参数
```javascript
//定义一个学号数组
[
	17034480126 
]
```
#### 预执行脚本
```javascript
暂无预执行脚本
```
#### 后执行脚本
```javascript
暂无后执行脚本
```
## /成绩
```text
暂无描述
```
#### 公共Header参数
参数名 | 示例值 | 参数描述
--- | --- | ---
暂无参数
#### 公共Query参数
参数名 | 示例值 | 参数描述
--- | --- | ---
暂无参数
#### 公共Body参数
参数名 | 示例值 | 参数描述
--- | --- | ---
暂无参数
#### 预执行脚本
```javascript
暂无预执行脚本
```
#### 后执行脚本
```javascript
暂无后执行脚本
```
## /成绩/获取成绩
```text
暂无描述
```
#### 接口状态
> 开发中

#### 接口URL
> localhost:8080/api/examination

#### 请求方式
> POST

#### Content-Type
> form-data

#### 请求Body参数
参数名 | 示例值 | 参数类型 | 是否必填 | 参数描述
--- | --- | --- | --- | ---
studentId | 17034480111 | Text | 是 | 学号
studentPassword |  | Text | 是 | 教务系统密码
schoolYear | 2017-2018-1 | Text | 是 | 学年学期(形如: 2017-2018-1 其中2017-2018代表2017-201学年 1代表上学期 2代表下学期
#### 预执行脚本
```javascript
暂无预执行脚本
```
#### 后执行脚本
```javascript
暂无后执行脚本
```
## /成绩/更新成绩
```text
暂无描述
```
#### 接口状态
> 开发中

#### 接口URL
> localhost:8080/api/examination

#### 请求方式
> PUT

#### Content-Type
> form-data

#### 请求Body参数
参数名 | 示例值 | 参数类型 | 是否必填 | 参数描述
--- | --- | --- | --- | ---
studentId | 17034480111 | Text | 是 | 学号
studentPassword |  | Text | 是 | 教务系统密码
schoolYear | 2017-2018-1 | Text | 是 | 学年学期(形如: 2017-2018-1 其中2017-2018代表2017-201学年 1代表上学期 2代表下学期
#### 预执行脚本
```javascript
暂无预执行脚本
```
#### 后执行脚本
```javascript
暂无后执行脚本
```
## /成绩/删除成绩
```text
暂无描述
```
#### 接口状态
> 开发中

#### 接口URL
> localhost:8080/api/examination

#### 请求方式
> DELETE

#### Content-Type
> json

#### 请求Body参数
```javascript
//定义一个学号数组
[
	17034480126 
]
```
#### 预执行脚本
```javascript
暂无预执行脚本
```
#### 后执行脚本
```javascript
暂无后执行脚本
```