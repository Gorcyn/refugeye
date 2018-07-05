package com.refugeye.data;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static android.support.test.InstrumentationRegistry.getTargetContext;

import com.refugeye.R;
import com.refugeye.data.model.Picto;
import com.refugeye.data.repository.PictoRepository;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class PictoRepositoryTest {

    private Context context;
    private PictoRepository pictoRepository;

    @Before
    public void initTargetContext() {
        context = getTargetContext();
        assertThat(context, notNullValue());
    }

    @Before
    public void createPictoRepository() {
        pictoRepository = new PictoRepository(context);
    }

    @Test
    public void pictoRepository_loadAllIcons() {
        List<Picto> pictoList = pictoRepository.getPictoList();

        assertThat(pictoList, notNullValue());

        assertThat(pictoList.size(), is(152));

        assertThat(pictoList.get(11).getResId(), is(R.drawable.all_icons_12));
        assertThat("traducción", isIn(pictoList.get(11).getNames()));

        assertThat(pictoList.get(53).getResId(), is(R.drawable.all_icons_54));
        assertThat("доктор", isIn(pictoList.get(53).getNames()));

        assertThat(pictoList.get(149).getResId(), is(R.drawable.all_icons2_54));
        assertThat("قرية", isIn(pictoList.get(149).getNames()));
    }

    @Test
    public void pictoRepository_filterIconsWithP() {
        List<Picto> pictoList = pictoRepository.findWithNameContaining("p");

        assertThat(pictoList, notNullValue());

        assertThat(pictoList.size(), is(84));

        assertThat(pictoList.get(12).getResId(), is(R.drawable.all_icons_26));
        assertThat("pistola", isIn(pictoList.get(12).getNames()));

        assertThat(pictoList.get(47).getResId(), is(R.drawable.all_icons_90));
        assertThat("溺水", isIn(pictoList.get(47).getNames()));
    }

    @Test
    public void pictoRepository_filterIconsWithPA() {
        List<Picto> pictoList = pictoRepository.findWithNameContaining("pa");

        assertThat(pictoList, notNullValue());

        assertThat(pictoList.size(), is(24));

        assertThat(pictoList.get(2).getResId(), is(R.drawable.all_icons_17));
        assertThat("дечија колица", isIn(pictoList.get(2).getNames()));

        assertThat(pictoList.get(11).getResId(), is(R.drawable.all_icons_83));
        assertThat("séquestré", isIn(pictoList.get(11).getNames()));
    }
}
