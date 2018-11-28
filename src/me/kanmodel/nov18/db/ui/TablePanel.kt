package me.kanmodel.nov18.db.ui

import me.kanmodel.nov18.db.database.DataQuery
import me.kanmodel.nov18.db.database.SqlQuery

import javax.swing.*
import javax.swing.table.DefaultTableModel
import java.awt.*
import java.awt.event.ItemEvent
import java.util.Vector
import java.util.regex.Pattern

internal class TablePanel : JPanel() {

    private val searchList = arrayOf("���", "����")
    private val searchBox = JComboBox(searchList)
    /**
     * ��������
     */
    private var searchType = SEARCH_BY_ID
    private var searchInfoFiled: JTextField? = null

    private val hBox0: Box = Box.createHorizontalBox()
    private val hBox1: Box = Box.createHorizontalBox()
    private val searchBtn = JButton("����")
    private val tableModel = object : DefaultTableModel() {
        override fun getColumnClass(column: Int): Class<*> {
            val returnValue: Class<*>
            if (column in 0..(columnCount - 1)) {
                returnValue = getValueAt(0, column).javaClass
            } else {
                returnValue = Any::class.java
            }
            return returnValue
        }
    }
    private val table: JTable = JTable(tableModel)
    private var dataVector: Vector<Vector<Any>>? = null
    private var columnName: Vector<String>? = null

    init {
        val vBox = Box.createVerticalBox()
        initButton()
        initTextFiled()
        initSearchBox()
        initTable()
        vBox.add(hBox0)
        vBox.add(Box.createVerticalStrut(20))
        vBox.add(hBox1)

        add(vBox)
    }

    private fun initTable() {
        table
        table.preferredScrollableViewportSize = Dimension(800, 600)
        table.autoResizeMode = JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS
        tableModel.addTableModelListener(UpdateTableListener(tableModel, table))

        val jScrollPane = JScrollPane(table)
        columnName = Vector()//�ֶ���
        columnName!!.add("���")
        columnName!!.add("Hitokoto")
        columnName!!.add("����")
        dataVector = Vector()
        dataVector = DataQuery.searchByFrom("")
        tableModel.setDataVector(dataVector, columnName)
        setColumn(table, 0, 60)
        setColumn(table, 1, 550)


//        val sorter = TableRowSorter<DefaultTableModel>(tableModel)
//         ����ֻ�е�һ�п�������������������
//        sorter.setSortable(0, true)
//        for (i in 1..2) {
//            sorter.setSortable(i, false)
//        }
//        table.rowSorter = sorter
        hBox1.add(jScrollPane)
    }

    private fun initButton() {

        val addBtn = JButton("����")
        addBtn.addActionListener {
            tableModel.insertRow(0, arrayOf<Any>(0, "", ""))
        }
        hBox0.add(addBtn)
        hBox0.add(Box.createHorizontalStrut(20))

        val deleteBtn = JButton("ɾ��")
        deleteBtn.addActionListener {
            val row = table.selectedRow
            if (row < 0) {
                JOptionPane.showMessageDialog(null, "��ѡ��Ҫɾ������")
            } else {
//                val id = table.getValueAt(row, 0).toString()
                deleteRow()
            }
        }
        hBox0.add(deleteBtn)
        hBox0.add(Box.createHorizontalStrut(20))


        val updateBtn = JButton("����")
        updateBtn.addActionListener {
            for (i in UpdateTableListener.updateList) {

            }
        }
        hBox0.add(updateBtn)
        hBox0.add(Box.createHorizontalStrut(20))

        searchBtn.addActionListener {
            // �����ı������ݲ�ѯ
            val content = searchInfoFiled!!.text.trim { it <= ' ' }

            when (searchType) {
                SEARCH_BY_ID -> {
                    val pattern = Pattern.compile("[0-9]*")//ʹ���������ʽ�ж��Ƿ�Ϊ����
                    val isNum = pattern.matcher(content)
                    if (isNum.matches()) {
                        dataVector = DataQuery.searchById(if (content == "") 0 else Integer.valueOf(content))
                        UpdateTableListener.clearHighLight(table)
                    } else {
                        JOptionPane.showMessageDialog(this, "��������ȷ����ID", "����", JOptionPane.WARNING_MESSAGE)
                    }
                }
                SEARCH_BY_FROM -> {
                    dataVector = DataQuery.searchByFrom(content)
                    UpdateTableListener.clearHighLight(table)
                }
            }
            val newTableModel = table.model as DefaultTableModel//
            if (dataVector!!.size == 0) {
                JOptionPane.showMessageDialog(this, "δ�ҵ���Ӧ����", "����", JOptionPane.WARNING_MESSAGE)
            } else {
                newTableModel.setDataVector(dataVector, columnName)
                setColumn(table, 0, 60)
                setColumn(table, 1, 550)
                table.updateUI()
            }
        }
    }

    private fun initSearchBox() {
        searchBox.addItemListener { e ->
            // ֻ����ѡ�е�״̬
            if (e.stateChange == ItemEvent.SELECTED) {
                searchType = searchBox.selectedIndex
                println("ѡ��: " + searchBox.selectedIndex + " = " + searchBox.selectedItem)
            }
        }
        val searchPane = JPanel()
        searchPane.add(searchBox)
        hBox0.add(searchPane)
        hBox0.add(Box.createHorizontalStrut(20))
        hBox0.add(searchBtn)
    }

    private fun initTextFiled() {
        searchInfoFiled = JTextField()
        searchInfoFiled!!.columns = 10
        hBox0.add(searchInfoFiled)
    }

    private fun setColumn(table: JTable, i: Int, width: Int) {
        val firstColumn = table.columnModel.getColumn(i)
        firstColumn.preferredWidth = width
        firstColumn.maxWidth = width
        firstColumn.minWidth = width
    }

    private fun deleteRow() {
        while (table.selectedRows.isNotEmpty()) {
            val row = table.selectedRow// ���ѡ��ĵ�һ��
            val id = table.getValueAt(row, 0).toString()
            val hikotoko = table.getValueAt(row, 1).toString()
            val from = table.getValueAt(row, 2).toString()
            println(id + hikotoko + from + row)
            (table.model as DefaultTableModel).removeRow(row)
            SqlQuery.deleteById(id)
        }
    }

    companion object {
        private const val SEARCH_BY_ID = 0
        private const val SEARCH_BY_FROM = 1
    }
}
