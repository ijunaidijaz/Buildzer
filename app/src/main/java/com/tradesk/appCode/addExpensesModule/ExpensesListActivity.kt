package com.tradesk.appCode.addExpensesModule

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.tradesk.R
import com.tradesk.appCode.ImageActivity
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.*
import com.tradesk.listeners.SingleListCLickListener
import com.tradesk.utils.extension.AllinOneDialog
import com.tradesk.utils.extension.toast
import kotlinx.android.synthetic.main.activity_expenses_list.*
import javax.inject.Inject

class ExpensesListActivity : BaseActivity(), SingleListCLickListener, IExpensesView {
    val mList = mutableListOf<Expenses>()
    val mProposalsAdapter by lazy { ExpensesAdapter(this,mList,this) }
    var selected_position=0

    @Inject
    lateinit var presenter: ExpensesPresenter<IExpensesView>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expenses_list)
        presenter.onAttach(this)

        rvExpenses.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        rvExpenses.adapter = mProposalsAdapter


        mIvBack.setOnClickListener { finish() }
        mIvAddExpense.setOnClickListener { startActivity(Intent(this,AddExpenseActivity::class.java)
            .putExtra("job_id",intent.getStringExtra("job_id"))) }
    }

    override fun onAdd(it: AddExpenseModel) {

    }

    override fun onList(it: ExpensesListModel) {
        if (it.data.expenses_list.isNotEmpty()){
            mList.clear()
            mList.addAll(it.data.expenses_list)
            mProposalsAdapter.notifyDataSetChanged()
        }else{
            toast("No expense added yet.")
        }
    }

    override fun onDelete(it: SuccessModel) {
        toast("Deleted successfully")
        mList.removeAt(selected_position)
        mProposalsAdapter.notifyDataSetChanged()
    }

    override fun onerror(data: String) {
       toast(data)
    }

    override fun onGeneratedToken(lastAction: String) {

    }

    override fun onSingleListClick(item: Any, position: Int) {
        if (item.equals("Delete")){
            AllinOneDialog(ttle = "Delete",
                msg = "Are you sure you want to Delete it ?",
                onLeftClick = {/*btn No click*/ },
                onRightClick = {/*btn Yes click*/
                    selected_position=position
                    if (isInternetConnected()){
                        presenter.deleteexpense(mList.get(position)._id)
                    }
                })
        }else if (item.equals("Image")){
           startActivity(Intent(this,ImageActivity::class.java)
               .putExtra("expense","Expenses")
               .putExtra("image",mList[position].image)
           )
        }else {
            var expenses:Expenses= item as Expenses;
            val builder = GsonBuilder()
            val gson = builder.create()
            var string=gson.toJson(expenses);
            startActivity(Intent(this,AddExpenseActivity::class.java)
                .putExtra("job_id",intent.getStringExtra("job_id"))
                .putExtra("expenses",string))

        }
    }

    override fun onResume() {
        super.onResume()
        if (isInternetConnected()){
            presenter.getExpenseslist("1","20",intent.getStringExtra("job_id").toString())
        }
    }
}