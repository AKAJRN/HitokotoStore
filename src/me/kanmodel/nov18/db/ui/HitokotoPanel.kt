package me.kanmodel.nov18.db.ui

import javax.swing.*
import java.awt.event.ItemEvent

internal class HitokotoPanel : JPanel() {
    init {
        val typeList = arrayOf("����", "����", "author")
        val searchBox = JComboBox(typeList)
        searchBox.addItemListener { e ->
            // ֻ����ѡ�е�״̬
            if (e.stateChange == ItemEvent.SELECTED) {
                println("ѡ��: " + searchBox.selectedIndex + " = " + searchBox.selectedItem)
            }
        }
        val searchPane = JPanel()
        searchPane.add(searchBox)
        add(searchPane)
    }
}
