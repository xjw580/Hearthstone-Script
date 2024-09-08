package club.xiaojiawei.listener.log;

import club.xiaojiawei.bean.Deck;
import club.xiaojiawei.interfaces.closer.LogListenerCloser;
import club.xiaojiawei.data.SpringData;
import club.xiaojiawei.utils.PowerLogUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 套牌日志监听器
 * @author 肖嘉威
 * @date 2023/9/20 16:43
 */
@Slf4j
@Component
public class DeckLogListener extends AbstractLogListener implements LogListenerCloser {

    @Getter
    private static final LinkedList<Deck> DECKS = new LinkedList<>();

    @Autowired
    public DeckLogListener(SpringData springData) {
        super(springData.getDeckLogName(), 0, 1500L, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void readOldLog() throws IOException {
        String line;
        while ((line = accessFile.readLine()) != null){
            if (line.contains("Deck Contents Received")){
                dealReceived();
            }else if (line.contains("Finished Editing Deck")){
                dealEditing();
            }
        }
    }

    private void dealReceived() throws IOException {
        DECKS.clear();
        String line;
        long filePointer = accessFile.getFilePointer();
        while ((line = accessFile.readLine()) != null){
            if (!line.contains("#")){
                accessFile.seek(filePointer);
                break;
            }
            DECKS.addFirst(createDeck(line));
            filePointer = accessFile.getFilePointer();
        }
    }

    private void dealEditing() throws IOException {
        Deck deck = createDeck(accessFile.readLine());
        boolean exist = false;
        for (Deck d : DECKS) {
            if (Objects.equals(d.getId(), deck.getId())){
                d.setName(deck.getName());
                d.setCode(deck.getCode());
                exist = true;
                break;
            }
        }
        if (!exist){
            DECKS.addFirst(deck);
        }
    }

    private Deck createDeck(String line) throws IOException {
        return new Deck(
                PowerLogUtil.iso88591_To_utf8(line.substring(line.indexOf("#") + 4)),
                (line = accessFile.readLine()).substring(line.indexOf("#") + 11),
                (line = accessFile.readLine()).substring(line.lastIndexOf(" ") + 1));
    }

    @Override
    protected void listenLog() throws IOException {
        readOldLog();
    }

    @Override
    public void closeLogListener() {
        cancelListener();
    }

}
