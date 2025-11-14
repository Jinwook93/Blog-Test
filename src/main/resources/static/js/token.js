const token = searchParam('token')

if (token) {
    localStorage.setItem("access_token", token)
}

function searchParam(key) {
    return new URLSearchParams(location.search).get(key);
}

//- location.search는 현재 페이지 URL의 쿼리 문자열 부분을 가져옵니다.
//예: ?token=abc123&user=jinwook
//- new URLSearchParams(...)는 이 쿼리 문자열을 파싱해서 key-value 형태로 다룰 수 있는 객체를 생성합니다.
//- .get(key)는 해당 key에 대응하는 값을 반환합니다.
