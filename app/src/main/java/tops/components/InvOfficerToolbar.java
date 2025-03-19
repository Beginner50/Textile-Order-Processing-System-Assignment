package tops.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InvOfficerToolbar extends AbstractToolbar {
    private ButtonHandler crudButtonHandler;

    public InvOfficerToolbar(){
        crudButtonHandler = new ButtonHandler();
        this.addCrudButton("UpdateItemStock", ButtonFactory.createButton("Update item stock level", "edit.png"), crudButtonHandler);
    }

    @Override
    public void loadConfiguration(String tabName, Table table) {
        super.loadConfiguration(tabName, table);

        switch (tabName){
            case "Item":
                crudButtons.get("UpdateItemStock").setVisible(true);
        }
    }

    class ButtonHandler implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent actionEvent) {

        }
    }
}
