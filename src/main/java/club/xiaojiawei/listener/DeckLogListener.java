package club.xiaojiawei.listener;

import club.xiaojiawei.bean.Deck;
import club.xiaojiawei.data.SpringData;
import club.xiaojiawei.utils.PowerLogUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author 肖嘉威
 * @date 2023/9/20 16:43
 * @msg
 */
@Slf4j
@Component
public class DeckLogListener extends AbstractLogListener{
    @Autowired
    public DeckLogListener(SpringData springData) {
        super(springData.getDeckLogName(), 0, 1500, TimeUnit.MILLISECONDS);
    }

    @Getter
    private static final LinkedList<Deck> decks = new LinkedList<>();

    @Override
    protected void readOldLog() {
        String line;
        try {
            while ((line = accessFile.readLine()) != null){
                if (line.contains("Deck Contents Received")){
                    dealReceived();
                }else if (line.contains("Finished Editing Deck")){
                    dealEditing();
                }
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void dealReceived(){
        decks.clear();
        String line;
        try {
            long filePointer = accessFile.getFilePointer();
            while ((line = accessFile.readLine()) != null){
                if (!line.contains("#")){
                    accessFile.seek(filePointer);
                    break;
                }
                decks.addFirst(createDeck(line));
                filePointer = accessFile.getFilePointer();
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void dealEditing(){
        try {
            Deck deck = createDeck(accessFile.readLine());
            boolean exist = false;
            for (Deck d : decks) {
                if (Objects.equals(d.getId(), deck.getId())){
                    d.setName(deck.getName());
                    d.setCode(deck.getCode());
                    exist = true;
                    break;
                }
            }
            if (!exist){
                decks.addFirst(deck);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Deck createDeck(String line){
        try {
            return new Deck(
                    PowerLogUtil.iso88591_To_utf8(line.substring(line.indexOf("#") + 4)),
                    (line = accessFile.readLine()).substring(line.indexOf("#") + 11),
                    (line = accessFile.readLine()).substring(line.lastIndexOf(" ") + 1));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void listenLog() {
        readOldLog();
    }
}
