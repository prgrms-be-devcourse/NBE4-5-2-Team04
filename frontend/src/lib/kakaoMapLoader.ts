const KAKAO_MAP_SCRIPT_ID = "kakao-map-script";

export function loadKakaoMap(callback: () => void) {
    if (typeof window === "undefined") return;

    if (window.kakao && window.kakao.maps) {
        console.log("카카오맵 이미 로드됨");
        callback();
        return;
    }

    if (document.getElementById(KAKAO_MAP_SCRIPT_ID)) {
        console.log("기존 카카오맵 스크립트 발견");
        document.getElementById(KAKAO_MAP_SCRIPT_ID)!.addEventListener("load", callback);
        return;
    }

    console.log("새로운 카카오맵 스크립트 추가");
    const script = document.createElement("script");
    script.id = KAKAO_MAP_SCRIPT_ID;
    script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${process.env.NEXT_PUBLIC_KAKAO_API_KEY}&libraries=services&autoload=false`;
    script.async = true;
    script.onload = () => {
        console.log("카카오맵 스크립트 로드 완료");
        callback();
    };
    document.head.appendChild(script);
}