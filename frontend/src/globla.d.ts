interface Window {
    kakao: any;
  }
  
  declare namespace kakao {
    namespace maps {
      class LatLng {
        constructor(lat: number, lng: number);
        getLat(): number;
        getLng(): number;
      }
  
      class Map {
        constructor(container: HTMLElement, options: { center: LatLng; level: number });
      }
  
      namespace services {
        class Places {
          keywordSearch(
            keyword: string,
            callback: (data: PlacesSearchResult, status: Status) => void
          ): void;
        }
  
        interface PlacesSearchResultItem {
          id: string;
          place_name: string;
          x: string; // 경도
          y: string; // 위도
          address_name: string;
          category_group_name?: string;
        }
  
        type PlacesSearchResult = PlacesSearchResultItem[];
  
        enum Status {
          OK = "OK",
          ZERO_RESULT = "ZERO_RESULT",
          ERROR = "ERROR",
        }
      }
    }
  }