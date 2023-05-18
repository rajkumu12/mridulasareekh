package com.educamp.eyeson.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.educamp.eyeson.R
import com.educamp.eyeson.databinding.FragmentPhoneInfoBinding


class PhoneInfoFragment : Fragment() {

    lateinit var binding: FragmentPhoneInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentPhoneInfoBinding.inflate(layoutInflater)

        return  binding.root
    }
}