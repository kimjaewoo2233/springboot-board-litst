package org.example.b01.board.domain;


import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "Reply",indexes = {
        @Index(name = "idx_reply_board_bno",columnList = "board_bno")
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Reply extends BaseEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long rno;

        @ManyToOne(fetch = FetchType.LAZY)
        @ToString.Exclude
        private Board board;

        private String replyText;

        private String replyer;

        public void changeTest(String replyText){
                this.replyText = replyText;
        }
}
