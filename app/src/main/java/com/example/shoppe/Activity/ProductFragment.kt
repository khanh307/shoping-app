package com.example.shoppe.Activity

import android.annotation.SuppressLint
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.AdapterView.OnItemClickListener
import androidx.core.os.HandlerCompat.postDelayed
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.shoppe.Adapter.ProductAdapter
import com.example.shoppe.Data.Product
import com.example.shoppe.Listener.IClickItemListener
import com.example.shoppe.Listener.PaginationScrollListener
import com.example.shoppe.R
import com.example.shoppe.Util.Server
import org.json.JSONArray
import org.json.JSONObject

class ProductFragment : Fragment(){

    lateinit var mMainActivity: MainActivity

    lateinit var adapterProduct: ProductAdapter
    var arrayProduct: ArrayList<Product> = ArrayList()
    lateinit var type: String
    lateinit var viewMain: RecyclerView
    var gridLayoutManager = GridLayoutManager(context, 2)

    private var isLoading = false;
    private var isLastPage = false;
    var page: Int = 1

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_product, container, false)
        viewMain = view.findViewById(R.id.viewMain)
        mMainActivity = activity as MainActivity


        var bundle = arguments
        if(bundle != null){
            type = bundle.getString("id", "-1")
        }
        viewMain.setHasFixedSize(true)
        viewMain.layoutManager = gridLayoutManager

        adapterProduct = ProductAdapter(requireContext(), arrayProduct,
            object : IClickItemListener {
                override fun clickItem(product: Product) {
                    mMainActivity.intentActivity(product)
                }
            })

        viewMain.adapter = adapterProduct
        getData(page)

        return view
    }




    private fun getData(page: Int) {
        var requestQueue: RequestQueue = Volley.newRequestQueue(context)
        var path: String = Server.pathProduct + page.toString()

        var stringRequest: StringRequest = object : StringRequest(Request.Method.POST, path, Response.Listener { response ->
            if(response != null && response.length > 0){
                var jsonArray: JSONArray = JSONArray(response)
                for(i in 0 until jsonArray.length()){
                    var jsonObject: JSONObject = jsonArray.getJSONObject(i)
                    var id = jsonObject.getInt("id")
                    var name = jsonObject.getString("name")
                    var image = jsonObject.getString("image")
                    image = image.replace("localhost:8012", Server.localhost)
                    var price = jsonObject.getDouble("price")
                    var detail = jsonObject.getString("detail")
                    var type = jsonObject.getInt("idtype")
                    arrayProduct.add(Product(id, name, image, price, detail, type))
                    adapterProduct.notifyDataSetChanged()
                }
            }
        }, Response.ErrorListener {

        }){
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("id", type)
                return params
            }
        }

        requestQueue.add(stringRequest)
    }


}