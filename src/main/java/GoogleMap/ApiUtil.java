package GoogleMap;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.*;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TransitMode;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.List;

public class ApiUtil {
    private GeoApiContext context;
    private String apiKey;

    ApiUtil(String apiKey) {
        this.apiKey = apiKey;
        this.context = new GeoApiContext.Builder()
                .apiKey(this.apiKey) // ここで適用。
                .build();
    }

    public GeoApiContext getContext() {
        return context;
    }

    public String getApiKey() {
        return apiKey;
    }

    /**
     * cnt: ここでAPIキーなどの情報。現状はApiUtilクラスにて取得している。
     * origin: 出発地
     * dest: 目的地
     *
     * 変えてくる値: DirectionsResult型のデータ
     */
    public DirectionsResult getDirect(GeoApiContext cnt, String origin, String dest,TravelMode travelMode) {
        DirectionsResult result = null;
        try {
            if(travelMode == TravelMode.TRANSIT){
                result = DirectionsApi
                        .getDirections(cnt, origin, dest) // ここで値を適用
                        .mode(travelMode) // 以下オプションを複数付けられる。
                        .language("ja")
                        .transitMode(TransitMode.RAIL)
                        .await(); // 最後にawaitをつけて終わる。
            }else {
                result = DirectionsApi
                        .getDirections(cnt, origin, dest) // ここで値を適用
                        .mode(travelMode) // 以下オプションを複数付けられる。
                        .language("ja")
                        .await(); // 最後にawaitをつけて終わる。
            }
            // DirectionsApiにつけることができるオプションは
            // https://googlemaps.github.io/google-maps-services-java/v0.2.3/javadoc/
            // を参照し、左下の欄の DirectionApiを参照すると、
            // getDirections関数の返り値の型が DirectionsApiRequest と分かることから、
            // DirectionsApiRequest に実装されてる関数がオプションとして使える事がわかる。

            // この関数はどんな関数なんだろう、と疑問が湧いた場合は、
            // 関数をクリックすると、関数の説明が書いてあるのでそれを参考にする。
            // 例) DirectionsApiRequestクラスのlanguage関数の説明は ->
            // "The language in which to return results. Note that we often update supported languages so this list may not be exhaustive."
            // と書いてあり、"帰ってくる値に対して言語をする関数である。サポートしてる言語ついては更新頻繁にするやで。" とわかる。
            // なお、See Also よりサポートされている言語一覧が書いてあるページに飛べるようである。

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static com.google.code.geocoder.model.LatLng geocode(String address) {
        final Geocoder geocoder = new Geocoder();
        GeocoderRequest req = new GeocoderRequestBuilder()
                .setAddress(address)
                .setLanguage("ja")
                .getGeocoderRequest();
        GeocodeResponse res = geocoder.geocode(req);
        GeocoderStatus status = res.getStatus();

        switch (status) {
            case ZERO_RESULTS:
                return null;
            case OVER_QUERY_LIMIT:
            case REQUEST_DENIED:
            case INVALID_REQUEST:
            case UNKNOWN_ERROR:
            case ERROR:
                throw new RuntimeException(status.value());
            default:
        }

        List<GeocoderResult> results = res.getResults();
        for (GeocoderResult result : results) {
            GeocoderGeometry geometry = result.getGeometry();
            return geometry.getLocation();
        }

        return null;
    }
}

