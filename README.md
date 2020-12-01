## 分析请求，确定思路

## 请求分析

1. 获取对应仓库的fork列表
https://github.com/zhegexiaohuozi/SeimiCrawler/network/members
https://github.com/{bolong/repoName}/network/members

2. 遍历fork的每个仓库地址，仓库地址首页的提交信息。找到最新更新的commit hash
https://github.com/{forkUsername}/SeimiCrawler

3. 验证最新的提交是否存在于被fork仓库
https://github.com/zhegexiaohuozi/SeimiCrawler/commit/{commitId}

4. 列出所有最新的提交日志。根据上个请求提示的数量。截取前面几个commit log
https://github.com/fanlushuai/SeimiCrawler/commits/master