
// 获取url参数
export function getQueryString(key){
  let searchString = document.location.search.toString();
  let returnValue = '';
  if (searchString.substr(0, 1) === '?' && searchString.length > 1) {
    let queryString = searchString.substring(1, searchString.length);
    let queryList = queryString.split('&');
    for (let i = 0; i < queryList.length; i++) {
      let oneQuery = queryList[i].split('=');
      if (oneQuery[0] === key && oneQuery.length === 2) {
        returnValue = oneQuery[1];
      }
    }
  }
  return returnValue;
}