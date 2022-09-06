package org.example.b01.board.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {

        @Builder.Default
        private int page = 1;

        @Builder.Default
        private int size = 10;

        private String type;    //검색종류는 t,c,w,tc,tw,twc

        private String keyword;

        private String link;

        public String[]  getTypes(){
            if(type == null || type.isEmpty()){
                return null;
            }
            return type.split("");
        }
        //repository 에서 String[]로 처리하기 때문에 type이라는 문자열을 배열로 반환해주는 기능이 필요
        //Pageable 타입을 반환하는 기능도 있으면 편리하므로 메소드 구현

        public Pageable getPageable(String... props){   //여러개가 올 수도 있고 아닐 수도 있기에 ... 파라미터를 사용했다.
            return PageRequest.of(this.page-1,this.size, Sort.by(props).descending());
        }

        public String getLink(){
            if(link == null){
                StringBuilder builder = new StringBuilder();

                builder.append("page="+this.page);
                builder.append("&size="+this.size);

                if(type != null && type.length() > 0){
                    builder.append("&type="+type);
                }

                if(keyword != null){
                    try{
                        builder.append("&keyword="+ URLEncoder.encode(keyword,"UTF-8"));
                    }catch (UnsupportedEncodingException e){

                    }
                }
                link = builder.toString();
            }
            return link;
        }
}
