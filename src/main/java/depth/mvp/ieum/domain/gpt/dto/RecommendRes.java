package depth.mvp.ieum.domain.gpt.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RecommendRes {

    private List<String> nickname;
}
