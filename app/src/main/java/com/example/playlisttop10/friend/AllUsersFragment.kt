package com.example.playlisttop10.friend

import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.playlisttop10.R
import com.example.playlisttop10.UserRepository
import com.example.playlisttop10.databinding.FragmentAllUsersBinding

class AllUsersFragment : Fragment() {
    private var _binding: FragmentAllUsersBinding? = null
    private val binding get() = _binding!!
    private lateinit var allUsersViewModel: AllUsersViewModel

    private lateinit var cv_friends: ComposeView
    private lateinit var btn_playlist: ImageButton
    private lateinit var btn_users: ImageButton
    private lateinit var btn_like: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        allUsersViewModel = ViewModelProvider(requireActivity())[AllUsersViewModel::class.java]
        allUsersViewModel.loadFriends()

        _binding = FragmentAllUsersBinding.inflate(inflater, container, false)

        cv_friends = binding.allCvList
        btn_playlist = binding.allBtnPlaylist
        btn_users = binding.allBtnUsers
        btn_like = binding.allBtnFavorite

        btn_users.setColorFilter(ContextCompat.getColor(requireContext(), R.color.light_blue))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_playlist.setOnClickListener {
            findNavController().navigate(R.id.action_allUsersFragment_to_playlistFragment)
        }

        btn_like.setOnClickListener {
            findNavController().navigate(R.id.action_allUsersFragment_to_favoriteFriendFragment)
        }

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                UserRepository.clear()
                findNavController().navigate(R.id.action_allUsersFragment_to_loginFragment)
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        allUsersViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            if (it != null && it != "") {
                Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
            }
        })

        allUsersViewModel.isUserListLoaded.observe(viewLifecycleOwner, Observer {
            if (it) {
                cv_friends.setContent {
                    val friendIdList = allUsersViewModel.friendIdList
                    friendIdList.remove(UserRepository.currUser!!.id)

                    Column(
                        Modifier
                            .padding(10.dp)
                    ) {
                        ElementList(friendIdList)
                    }
                }
            }
        })
    }

    private fun onClick(id: String) {
        val action = AllUsersFragmentDirections.actionAllUsersFragmentToPlaylistByUserFragment(id)
        findNavController().navigate(action)
    }

    @Composable
    fun ElementList(
        elementList: List<String>
    ) {
        LazyColumn(content = {
            items(count = elementList.size) {
                Element(elementList[it])
            }
        })
    }

    @Composable
    fun Element(id: String) {
        Card(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth()
                .clickable { onClick(id) },
            elevation = 2.dp,
            backgroundColor = Color.White,
            shape = RoundedCornerShape(corner = CornerSize(16.dp))
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = id, fontSize = 25.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Left
                )
                Text(
                    text = "${allUsersViewModel.getNumberOfLikesById(id)} likes", fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}