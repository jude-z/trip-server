package core.infra.projection.keep;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class KeepsResponse {
    private String code;
    private String message;
    private List<KeepElement> list;
    private boolean hasNext;

    public static KeepsResponse of(String code, String message,List<KeepElement> list,boolean hasNext){
        return new KeepsResponse(code, message,list,hasNext);
    }
}
